package dev.nonoxy.d2buildhelper.features.guides.presentation.models

sealed class GuidesEvent {
    class HeroSearchValueChanged(val newValue: String) : GuidesEvent()
    data object HeroSearchDialogClicked : GuidesEvent()
    class SelectHeroInSearchDialog(val heroId: Short) : GuidesEvent()
}