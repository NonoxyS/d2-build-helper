package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data

import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion
import dev.nonoxy.d2buildhelper.core.resources.domain.models.DotaConstants
import dev.nonoxy.d2buildhelper.core.resources.domain.repository.ResourcesRepository

internal class FakeResourcesRepository(
    private val constants: DotaConstants,
    private val getResult: Result<DotaConstants> = Result.success(constants),
    private val refreshResult: Result<DotaConstants> = Result.success(constants),
) : ResourcesRepository {

    var lastExpectedVersion: GameVersion? = null
        private set

    override suspend fun getDotaConstants(): Result<DotaConstants> = getResult

    override suspend fun refreshDotaConstants(expectedVersion: GameVersion?): Result<DotaConstants> {
        lastExpectedVersion = expectedVersion
        return refreshResult
    }
}
