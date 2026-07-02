package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

enum class TalentSide { LEFT, RIGHT }

data class UiTalentTier(
    val tier: Int,
    val taken: Boolean,
    val side: TalentSide?,
)
