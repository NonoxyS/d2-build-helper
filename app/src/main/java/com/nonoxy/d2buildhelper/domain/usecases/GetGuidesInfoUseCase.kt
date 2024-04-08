package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient

class GetGuidesInfoUseCase(private val heroBuildClient: HeroBuildClient) {

    suspend fun execute(): List<HeroGuideInfo> {
        return heroBuildClient.getGuidesInfo()
    }
}