package dev.nonoxy.d2buildhelper.feature.guides.presentation.mappers

import dev.nonoxy.d2buildhelper.common.mappers.Mapper
import dev.nonoxy.d2buildhelper.feature.guides.api.store.GuidesStore
import dev.nonoxy.d2buildhelper.feature.guides.presentation.models.UiGuidesState

interface UiGuidesStateMapper : Mapper<GuidesStore.State, UiGuidesState>

class UiGuidesStateMapperImpl : UiGuidesStateMapper {
    override fun map(item: GuidesStore.State): UiGuidesState = with(item) {
        UiGuidesState(
            guides = guides,
            imageResources = imageResources,
            heroSearchValue = heroSearchValue,
            heroSearchFiltered = heroSearchFiltered,
            isLoading = isLoading,
            isError = isError,
        )
    }
}
