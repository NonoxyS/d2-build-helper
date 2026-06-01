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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class GuidesExecutor(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) : BaseExecutor<Intent, Action, State, Message, Label>(mainContext = dispatchers.main) {

    override suspend fun suspendExecuteAction(action: Action) {
        when (action) {
            Action.LoadInitial -> loadForCurrentFilters()
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
                loadForCurrentFilters()
            }

            Intent.OnRetry -> loadForCurrentFilters()
        }
    }

    private suspend fun applyFilter(value: FilterValue) {
        val current = state().filters
        val newFilters = when (value) {
            is FilterValue.Hero -> current.copy(heroId = current.heroId.toggle(value.heroId))
            is FilterValue.Position -> current.copy(position = current.position.toggle(value.position))
            is FilterValue.Side -> current.copy(isRadiant = current.isRadiant.toggle(value.isRadiant))
        }
        dispatch(Message.SetFilters(newFilters))
        dispatch(Message.SetActivePicker(null))
        dispatch(Message.SetPickerSearch(""))
        loadForCurrentFilters()
    }

    private fun <T> T?.toggle(selected: T): T? = if (this == selected) null else selected

    private suspend fun resetFilter(kind: GuidesFilterKind) {
        val cleared = when (kind) {
            GuidesFilterKind.Hero -> state().filters.copy(heroId = null)
            GuidesFilterKind.Position -> state().filters.copy(position = null)
            GuidesFilterKind.Side -> state().filters.copy(isRadiant = null)
        }
        dispatch(Message.SetFilters(cleared))
        loadForCurrentFilters()
    }

    private suspend fun loadForCurrentFilters() {
        val heroId = state().filters.heroId
        if (heroId != null) {
            renderGuidesPage { guidesRepository.getHeroGuides(heroId) }
        } else {
            renderGuidesPage { guidesRepository.getGuides() }
        }
    }

    private suspend fun renderGuidesPage(fetcher: suspend () -> Result<GuidesPage>) {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))

        fetchGuidesWithSyncedConstants(fetcher).fold(
            onSuccess = { loaded ->
                dispatch(Message.SetConstants(loaded.constants))
                dispatch(Message.SetGuides(loaded.guidesPage.guides))
                dispatch(Message.SetLoading(false))
            },
            onFailure = { error ->
                Napier.e(throwable = error, message = "GuidesExecutor: failed to load guides page")
                dispatch(Message.SetError(true))
                dispatch(Message.SetLoading(false))
            },
        )
    }

    private suspend fun fetchGuidesWithSyncedConstants(
        fetcher: suspend () -> Result<GuidesPage>,
    ): Result<GuidesAndConstants> = coroutineScope {
        val constantsDef = async { resourcesRepository.getDotaConstants() }
        val guidesDef = async { fetcher() }

        val cachedConstants = constantsDef
            .await()
            .getOrElse { error -> return@coroutineScope error.wrapResultFailure() }

        val guidesPage = guidesDef
            .await()
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
