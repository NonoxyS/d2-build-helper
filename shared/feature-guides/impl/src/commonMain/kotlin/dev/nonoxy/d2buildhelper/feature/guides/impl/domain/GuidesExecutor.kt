package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
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

    private var lastFailedRetry: (suspend () -> Unit)? = null

    override suspend fun suspendExecuteAction(action: Action) {
        when (action) {
            Action.LoadInitial -> loadInitial { guidesRepository.getGuides() }
            is Action.FilterByHero -> loadInitial { guidesRepository.getHeroGuides(action.heroId) }
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
                suspendExecuteAction(Action.LoadInitial)
            }
            Intent.OnRetry -> (lastFailedRetry ?: { suspendExecuteAction(Action.LoadInitial) }).invoke()
        }
    }

    private suspend fun loadInitial(fetchGuides: suspend () -> Result<GuidesPage>): Unit = coroutineScope {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))

        val constantsDef = async { resourcesRepository.getDotaConstants() }
        val guidesDef = async { fetchGuides() }

        val constantsResult = constantsDef.await()
        val guidesResult = guidesDef.await()

        val firstFailure = listOf(constantsResult, guidesResult).firstOrNull { it.isFailure }
        if (firstFailure != null) {
            Napier.e(throwable = firstFailure.exceptionOrNull(), message = "GuidesExecutor.loadInitial failed")
            lastFailedRetry = { loadInitial(fetchGuides) }
            dispatch(Message.SetError(true))
            dispatch(Message.SetLoading(false))
            return@coroutineScope
        }

        val cachedConstants = constantsResult.getOrThrow()
        val guidesPage = guidesResult.getOrThrow()

        val finalConstants: DotaConstants = if (cachedConstants.gameVersion == guidesPage.gameVersion) {
            cachedConstants
        } else {
            val refreshResult = resourcesRepository.refreshDotaConstants(expectedVersion = guidesPage.gameVersion)
            if (refreshResult.isFailure) {
                Napier.e(
                    throwable = refreshResult.exceptionOrNull(),
                    message = "GuidesExecutor.loadInitial: version-mismatch refresh failed; serving cached",
                )
                cachedConstants
            } else {
                refreshResult.getOrThrow()
            }
        }

        dispatch(
            Message.SetConstants(
                heroes = finalConstants.heroes,
                items = finalConstants.items,
                abilities = finalConstants.abilities,
                gameVersion = finalConstants.gameVersion,
            ),
        )
        dispatch(Message.SetGuides(guidesPage.guides))
        dispatch(Message.SetLoading(false))
        lastFailedRetry = null
    }

    private suspend fun applyFilter(value: FilterValue) {
        val newFilters = when (value) {
            is FilterValue.Hero -> state().filters.copy(heroId = value.heroId)
            is FilterValue.Position -> state().filters.copy(position = value.position)
            is FilterValue.Side -> state().filters.copy(isRadiant = value.isRadiant)
        }
        dispatch(Message.SetFilters(newFilters))
        dispatch(Message.SetActivePicker(null))
        dispatch(Message.SetPickerSearch(""))

        val heroId = newFilters.heroId
        if (heroId != null) {
            suspendExecuteAction(Action.FilterByHero(heroId))
        } else {
            suspendExecuteAction(Action.LoadInitial)
        }
    }

    private suspend fun resetFilter(kind: GuidesFilterKind) {
        val cleared = when (kind) {
            GuidesFilterKind.Hero -> state().filters.copy(heroId = null)
            GuidesFilterKind.Position -> state().filters.copy(position = null)
            GuidesFilterKind.Side -> state().filters.copy(isRadiant = null)
        }
        dispatch(Message.SetFilters(cleared))
        suspendExecuteAction(Action.LoadInitial)
    }
}
