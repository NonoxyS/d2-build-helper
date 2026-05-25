package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesLabel

interface UiGuidesLabelMapper : Mapper<GuidesStore.Label, UiGuidesLabel?>

@Suppress("UnusedParameter")
class UiGuidesLabelMapperImpl : UiGuidesLabelMapper {
    override fun map(item: GuidesStore.Label): UiGuidesLabel? = null
}
