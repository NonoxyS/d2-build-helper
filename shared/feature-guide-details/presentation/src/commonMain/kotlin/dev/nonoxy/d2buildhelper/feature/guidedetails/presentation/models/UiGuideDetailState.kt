package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models

data class UiGuideDetailState(
    val header: UiBuildHeader? = null,
    val skillBuild: UiSkillBuild? = null,
    val itemBuild: UiItemBuild? = null,
    val networth: UiNetworthCurve? = null,
    val lineup: UiLineup? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)
