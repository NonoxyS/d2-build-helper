package dev.nonoxy.d2buildhelper.features.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.UiGuidesLabel

interface UiGuidesLabelMapper : Mapper<GuidesStore.Label, UiGuidesLabel>

internal class UiGuidesLabelMapperImpl : UiGuidesLabelMapper {
    override fun map(item: GuidesStore.Label): UiGuidesLabel = when (item) {
        GuidesStore.Label.ShowHeroSearchDialog -> UiGuidesLabel.ShowHeroSearchDialog
    }
}
