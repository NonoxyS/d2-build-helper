package dev.nonoxy.d2buildhelper.core.resources.data

internal object IconUrls {
    private const val BASE = "https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react"

    fun hero(shortName: String): String = "$BASE/heroes/icons/$shortName.png"

    fun item(shortName: String): String = "$BASE/items/$shortName.png"

    fun ability(name: String): String = "$BASE/abilities/$name.png"
}
