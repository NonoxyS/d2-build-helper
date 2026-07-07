package dev.nonoxy.d2buildhelper.core.domain.models

data class Item(
    val id: ItemId,
    val shortName: String,
    val displayName: String,
    val iconUrl: ImageUrl,
    val quality: String?,
    val isRecipe: Boolean,
    val components: List<ItemId>,
)
