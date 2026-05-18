package dev.nonoxy.d2buildhelper.features.guides.api.store

import com.arkivanov.mvikotlin.core.store.Store
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Intent
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.Label
import dev.nonoxy.d2buildhelper.features.guides.api.store.GuidesStore.State
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Guide
import dev.nonoxy.d2buildhelper.features.guides.domain.models.Hero
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ImageResources

interface GuidesStore : Store<Intent, State, Label> {

    data class State(
        val guides: List<Guide> = emptyList(),
        val imageResources: ImageResources? = null,
        val heroSearchValue: String = "",
        val heroSearchFiltered: Map<Hero, String> = emptyMap(),
        val isLoading: Boolean = true,
        val isError: Boolean = false,
    )

    sealed interface Intent {
        data class OnHeroSearchValueChange(val newValue: String) : Intent
        data object OnHeroSearchDialogClick : Intent
        data class OnHeroSelect(val heroId: Short) : Intent
        data object OnRetry : Intent
    }

    sealed interface Label {
        data object ShowHeroSearchDialog : Label
    }
}
