package dev.nonoxy.d2buildhelper.features.guides.presentation.models

sealed class GuidesAction {
    data object ShowHeroSearchDialog : GuidesAction()
}