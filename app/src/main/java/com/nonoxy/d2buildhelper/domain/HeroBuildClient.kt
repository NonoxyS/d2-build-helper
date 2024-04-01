package com.nonoxy.d2buildhelper.domain

import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild

interface HeroBuildClient {
    suspend fun getGuidesInfo(): List<HeroGuideInfo>
    suspend fun getHeroGuidesInfo(heroId: Short): List<HeroGuideInfo>
    suspend fun getHeroGuideBuild(matchId: Long, steamAccountId: Long): HeroGuideBuild
}