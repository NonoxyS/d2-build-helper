package com.nonoxy.d2buildhelper.domain.repository

interface ResourceClient {
    suspend fun getItemNameById(itemIds: List<Int>): MutableMap<Short, String>
    suspend fun getHeroNameById(heroIds: List<Int>): MutableMap<Short, MutableMap<String, String>>
    suspend fun getHeroImageUrlByName(heroName: String): String
    suspend fun getItemImageUrlByName(itemName: String): String
    suspend fun getUtilImageUrlByName(utilName: String): String
}