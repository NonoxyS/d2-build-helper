package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.mvikotlin.BaseExecutor
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.FilterValue
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesPage
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory.Action
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.GuidesStoreFactory.Message
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class GuidesExecutor(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) : BaseExecutor<Intent, Action, State, Message, Label>(mainContext = dispatchers.main) {

    private var loadJob: Job? = null

    override suspend fun suspendExecuteAction(action: Action) {
        when (action) {
            Action.LoadInitial -> fullLoad()
        }
    }

    override suspend fun suspendExecuteIntent(intent: Intent) {
        when (intent) {
            is Intent.OnFilterChipClick -> dispatch(Message.SetActivePicker(intent.kind))
            Intent.OnPickerDismiss -> {
                dispatch(Message.SetActivePicker(null))
                dispatch(Message.SetPickerSearch(""))
            }

            is Intent.OnPickerSearchChange -> dispatch(Message.SetPickerSearch(intent.value))
            is Intent.OnFilterApply -> applyFilter(intent.value)
            is Intent.OnFilterReset -> resetFilter(intent.kind)
            Intent.OnFiltersResetAll -> {
                dispatch(Message.SetFilters(GuidesFilters()))
                fullLoad()
            }

            Intent.OnRetry -> fullLoad()
            Intent.OnRefresh -> refresh()
            Intent.OnLoadMore -> loadMore()
        }
    }

    private fun applyFilter(value: FilterValue) {
        val current = state().filters
        val newFilters = when (value) {
            is FilterValue.Hero -> current.copy(heroId = current.heroId.toggle(value.heroId))
            is FilterValue.Position -> current.copy(position = current.position.toggle(value.position))
            is FilterValue.Side -> current.copy(isRadiant = current.isRadiant.toggle(value.isRadiant))
        }
        dispatch(Message.SetFilters(newFilters))
        dispatch(Message.SetActivePicker(null))
        dispatch(Message.SetPickerSearch(""))
        fullLoad()
    }

    private fun <T> T?.toggle(selected: T): T? = if (this == selected) null else selected

    private fun resetFilter(kind: GuidesFilterKind) {
        val cleared = when (kind) {
            GuidesFilterKind.Hero -> state().filters.copy(heroId = null)
            GuidesFilterKind.Position -> state().filters.copy(position = null)
            GuidesFilterKind.Side -> state().filters.copy(isRadiant = null)
        }
        dispatch(Message.SetFilters(cleared))
        fullLoad()
    }

    private fun fullLoad() {
        loadJob?.cancel()
        loadJob = scope.launch { runFullLoad() }
    }

    private fun refresh() {
        if (state().isRefreshing) return
        loadJob?.cancel()
        loadJob = scope.launch { runRefresh() }
    }

    private fun loadMore() {
        val current = state()
        if (!current.pagination.hasMore || current.isLoadingMore || current.isLoading || current.isRefreshing) {
            return
        }
        loadJob?.cancel()
        loadJob = scope.launch {
            runLoadMore(page = current.pagination.page + 1, filters = current.filters)
        }
    }

    private suspend fun runFullLoad() {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))
        dispatch(Message.SetLoadMoreError(false))

        fetchPageWithConstants(page = 0).fold(
            onSuccess = { loaded ->
                dispatch(Message.SetConstants(loaded.constants))
                dispatch(Message.SetGuides(loaded.guidesPage.guides))
                dispatch(Message.SetPagination(loaded.guidesPage.pagination))
                dispatch(Message.SetLoading(false))
            },
            onFailure = { error ->
                Napier.e(throwable = error, message = "GuidesExecutor: full load failed")
                dispatch(Message.SetError(true))
                dispatch(Message.SetLoading(false))
            },
        )
    }

    private suspend fun runRefresh() {
        dispatch(Message.SetRefreshing(true))
        dispatch(Message.SetLoadMoreError(false))

        fetchPageWithConstants(page = 0).fold(
            onSuccess = { loaded ->
                dispatch(Message.SetConstants(loaded.constants))
                dispatch(Message.SetGuides(loaded.guidesPage.guides))
                dispatch(Message.SetPagination(loaded.guidesPage.pagination))
                dispatch(Message.SetError(false))
                dispatch(Message.SetRefreshing(false))
            },
            onFailure = { error ->
                Napier.e(throwable = error, message = "GuidesExecutor: refresh failed")
                if (state().guides.isEmpty()) dispatch(Message.SetError(true))
                dispatch(Message.SetRefreshing(false))
            },
        )
    }

    private suspend fun runLoadMore(page: Int, filters: GuidesFilters) {
        dispatch(Message.SetLoadingMore(true))
        dispatch(Message.SetLoadMoreError(false))

        guidesRepository.getGuides(filters = filters, page = page).fold(
            onSuccess = { loaded ->
                dispatch(Message.AppendGuides(loaded.guides))
                dispatch(Message.SetPagination(loaded.pagination))
                dispatch(Message.SetLoadingMore(false))
            },
            onFailure = { error ->
                Napier.e(throwable = error, message = "GuidesExecutor: load-more failed")
                dispatch(Message.SetLoadingMore(false))
                dispatch(Message.SetLoadMoreError(true))
            },
        )
    }

    private suspend fun fetchPageWithConstants(page: Int): Result<GuidesAndConstants> = coroutineScope {
        val constantsDef = async { resourcesRepository.getDotaConstants() }
        val guidesDef = async { guidesRepository.getGuides(filters = state().filters, page = page) }

        val cachedConstants = constantsDef.await()
            .getOrElse { error -> return@coroutineScope error.wrapResultFailure() }
        val guidesPage = guidesDef.await()
            .getOrElse { error -> return@coroutineScope error.wrapResultFailure() }

        val finalConstants = syncConstantsToGuidesVersion(cachedConstants, guidesPage.gameVersion)
        GuidesAndConstants(constants = finalConstants, guidesPage = guidesPage).wrapResultSuccess()
    }

    private suspend fun syncConstantsToGuidesVersion(
        cached: DotaConstants,
        guidesVersion: GameVersion,
    ): DotaConstants {
        if (cached.gameVersion == guidesVersion) return cached
        return resourcesRepository.refreshDotaConstants(expectedVersion = guidesVersion).getOrElse { error ->
            Napier.e(
                throwable = error,
                message = "GuidesExecutor: version-mismatch refresh failed; serving cached",
            )
            cached
        }
    }

    private data class GuidesAndConstants(
        val constants: DotaConstants,
        val guidesPage: GuidesPage,
    )
}
