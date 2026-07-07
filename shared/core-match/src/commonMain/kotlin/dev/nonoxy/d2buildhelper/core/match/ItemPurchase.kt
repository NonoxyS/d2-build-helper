package dev.nonoxy.d2buildhelper.core.match

import dev.nonoxy.d2buildhelper.core.domain.models.ItemId

data class ItemPurchase(
    val itemId: ItemId,
    val time: Int?,
)
