package com.nonoxy.d2buildhelper.domain.repository

interface ResourceClient {

    // -> {itemId: shortName}
    suspend fun getItemNameById(itemIds: List<Int>): MutableMap<Short, String>

    // -> {heroId: {displayName: displayName, shortName: shortName} }
    suspend fun getHeroDetailsById(heroIds: List<Int>): MutableMap<Short, MutableMap<String, String>>

    // -> URL
    suspend fun getHeroImageUrlByName(heroName: String): String

    // -> URL
    suspend fun getItemImageUrlByName(itemName: String): String

    // -> URL
    suspend fun getUtilImageUrlByName(utilName: String): String

    suspend fun getEachHeroDetails(): MutableMap<Short, MutableMap<String, String>>
}