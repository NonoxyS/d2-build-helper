package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Intent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Label
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.State
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository

internal class GuideDetailStoreFactory(
    private val storeFactory: StoreFactory,
    private val guideDetailRepository: GuideDetailRepository,
    private val resourcesRepository: ResourcesRepository,
    private val dispatchers: CoroutineDispatchers,
) {

    fun create(matchId: Long, steamAccountId: Long): GuideDetailStore = object :
        GuideDetailStore,
        Store<Intent, State, Label> by storeFactory.create(
            name = "GuideDetailStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Action.LoadInitial),
            executorFactory = {
                GuideDetailExecutor(
                    matchId = matchId,
                    steamAccountId = steamAccountId,
                    guideDetailRepository = guideDetailRepository,
                    resourcesRepository = resourcesRepository,
                    dispatchers = dispatchers,
                )
            },
            reducer = GuideDetailReducer(),
        ) {}

    internal sealed interface Action {
        data object LoadInitial : Action
    }

    internal sealed interface Message {
        data class SetLoading(val isLoading: Boolean) : Message
        data class SetError(val isError: Boolean) : Message
        data class SetDetail(val detail: GuideDetail) : Message
        data class SetConstants(val constants: DotaConstants) : Message
    }
}
