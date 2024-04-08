package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient

class GetHeroGuidesInfoUseCase(private val heroBuildClient: HeroBuildClient) {

    suspend fun execute(heroId: Short): List<HeroGuideInfo> {
        return heroBuildClient.getHeroGuidesInfo(heroId)
    }
}