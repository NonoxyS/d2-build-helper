package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient

class GetHeroGuideBuildUseCase(private val heroBuildClient: HeroBuildClient) {

    suspend fun execute(matchId: Long, steamAccountId: Long): HeroGuideBuild {
        return heroBuildClient.getHeroGuideBuild(matchId, steamAccountId)
    }
}