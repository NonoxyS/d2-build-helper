package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultSuccess
import dev.nonoxy.d2buildhelper.core.mvikotlin.BaseExecutor
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.domain.models.GuideDetail
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Intent
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.Label
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore.State
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.GuideDetailStoreFactory.Action
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.GuideDetailStoreFactory.Message
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

internal class GuideDetailExecutor(
    private val matchId: Long,
    private val steamAccountId: Long,
    private val guideDetailRepository: GuideDetailRepository,
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
            Intent.OnRetry -> fullLoad()
        }
    }

    private fun fullLoad() {
        loadJob?.cancel()
        loadJob = scope.launch { runFullLoad() }
    }

    private suspend fun runFullLoad() {
        dispatch(Message.SetLoading(true))
        dispatch(Message.SetError(false))

        fetchDetailWithConstants().fold(
            onSuccess = { loaded ->
                dispatch(Message.SetConstants(loaded.constants))
                dispatch(Message.SetDetail(loaded.detail))
                dispatch(Message.SetLoading(false))
            },
            onFailure = { error ->
                Napier.e(throwable = error, message = "GuideDetailExecutor: full load failed")
                dispatch(Message.SetError(true))
                dispatch(Message.SetLoading(false))
            },
        )
    }

    private suspend fun fetchDetailWithConstants(): Result<DetailAndConstants> = coroutineScope {
        val constantsDef = async { resourcesRepository.getDotaConstants() }
        val detailDef = async { guideDetailRepository.getGuideDetail(matchId, steamAccountId) }

        val constants = constantsDef.await()
            .getOrElse { error -> return@coroutineScope error.wrapResultFailure() }
        val detail = detailDef.await()
            .getOrElse { error -> return@coroutineScope error.wrapResultFailure() }

        DetailAndConstants(constants = constants, detail = detail).wrapResultSuccess()
    }

    private data class DetailAndConstants(
        val constants: DotaConstants,
        val detail: GuideDetail,
    )
}
