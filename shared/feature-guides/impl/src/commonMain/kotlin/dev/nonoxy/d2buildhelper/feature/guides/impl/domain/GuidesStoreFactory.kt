package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Guide
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilterKind
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.GuidesFilters
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.models.Pagination
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository

internal class GuidesStoreFactory(
    private val storeFactory: StoreFactory,
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) {

    fun create(): GuidesStore = object :
        GuidesStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GuidesStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.LoadInitial),
            executorFactory = {
                GuidesExecutor(
                    guidesRepository = guidesRepository,
                    resourcesRepository = resourcesRepository,
                    dispatchers = dispatchers,
                )
            },
            reducer = GuidesReducer(),
        ) {}

    internal sealed interface Action {
        data object LoadInitial : Action
    }

    internal sealed interface Message {
        data class SetLoading(val isLoading: Boolean) : Message
        data class SetError(val isError: Boolean) : Message
        data class SetGuides(val guides: List<Guide>) : Message
        data class SetConstants(val constants: DotaConstants) : Message
        data class SetFilters(val filters: GuidesFilters) : Message
        data class SetActivePicker(val kind: GuidesFilterKind?) : Message
        data class SetPickerSearch(val value: String) : Message
        data class SetPagination(val pagination: Pagination) : Message
        data class AppendGuides(val guides: List<Guide>) : Message
        data class SetLoadingMore(val isLoadingMore: Boolean) : Message
        data class SetRefreshing(val isRefreshing: Boolean) : Message
        data class SetLoadMoreError(val isLoadMoreError: Boolean) : Message
    }
}
