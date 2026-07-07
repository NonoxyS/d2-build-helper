package dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.feature.guidedetails.api.store.GuideDetailStore
import dev.nonoxy.d2buildhelper.feature.guidedetails.presentation.models.UiGuideDetailLabel

interface UiGuideDetailLabelMapper : Mapper<GuideDetailStore.Label, UiGuideDetailLabel?>

class UiGuideDetailLabelMapperImpl : UiGuideDetailLabelMapper {
    override fun map(item: GuideDetailStore.Label): UiGuideDetailLabel? = null
}
