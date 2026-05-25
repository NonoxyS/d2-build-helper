package dev.nonoxy.d2buildhelper.feature.guides.api.domain

import dev.nonoxy.d2buildhelper.core.domain.models.GameVersion

data class GuidesPage(
    val gameVersion: GameVersion,
    val guides: List<Guide>,
)
