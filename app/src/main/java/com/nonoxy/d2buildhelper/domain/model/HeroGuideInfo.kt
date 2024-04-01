package com.nonoxy.d2buildhelper.domain.model

data class HeroGuideInfo(
    val heroId: Short,
    val guidesInfo: List<GuideInfo?>?
)

data class GuideInfo(
    val matchId: Long,
    val steamAccountId: Long
)
