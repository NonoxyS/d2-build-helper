package dev.nonoxy.d2buildhelper.features.guides.models

sealed class GuidesViewState {
    data object Loading : GuidesViewState()
    data object Error : GuidesViewState()
    data class Display(
        val guides: List<String>
    ) : GuidesViewState()
}