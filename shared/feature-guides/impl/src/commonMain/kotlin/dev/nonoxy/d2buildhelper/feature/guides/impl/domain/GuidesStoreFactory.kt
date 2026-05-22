package dev.nonoxy.d2buildhelper.feature.guides.impl.domain

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guides.impl.domain.repository.GuidesRepository
import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.feature.guides.api.domain.Guide
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.ImageResources

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
        data class FilterHeroes(val query: String) : Action
    }

    internal sealed interface Message {
        data class SetLoading(val isLoading: Boolean) : Message
        data class SetError(val isError: Boolean) : Message
        data class SetGuides(val guides: List<Guide>) : Message
        data class SetImageResources(val resources: ImageResources) : Message
        data class SetHeroSearchValue(val value: String) : Message
        data class SetHeroSearchFiltered(val filtered: Map<Hero, String>) : Message
    }
}
