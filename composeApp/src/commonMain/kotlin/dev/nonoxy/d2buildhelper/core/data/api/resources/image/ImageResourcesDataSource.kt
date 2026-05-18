package dev.nonoxy.d2buildhelper.core.data.api.resources.image

import Dota___Build_Helper.composeApp.BuildConfig
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.extensions.coRunCatching
import dev.nonoxy.d2buildhelper.common.extensions.wrapResultFailure
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.AbilityDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.HeroDto
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.ItemDto
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.withContext

internal class ImageResourcesDataSource(
    private val supabaseClient: SupabaseClient,
    private val dispatchers: CoroutineDispatchers,
) : ImageResourcesApi {
    private val supabaseImageStorage = supabaseClient.storage.from(IMAGES_BUCKET_ID)

    override suspend fun getHeroImageUrls(heroConstants: List<HeroDto>): Result<Map<HeroDto, String>> =
        withContext(dispatchers.io) {
            coRunCatching(
                tryBlock = {
                    val heroToUrl = mutableMapOf<HeroDto, String>()
                    supabaseImageStorage.list(HERO_IMAGES_FOLDER_PATH) { limit = 200 }
                        .forEach { bucketItem ->
                            val heroName = bucketItem.name.removeSuffix("_minimap_icon.png")
                            heroConstants.find { it.shortName == heroName }?.let { hero ->
                                heroToUrl[hero] =
                                    "${BuildConfig.STORAGE_HERO_ICONS_FOLDER_URL}${bucketItem.name}"
                            }
                        }
                    heroToUrl.toMap()
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "getHeroImageUrls failed")
                    throwable.wrapResultFailure()
                },
            )
        }

    override suspend fun getItemImageUrls(itemConstants: List<ItemDto>): Result<Map<ItemDto, String>> =
        withContext(dispatchers.io) {
            coRunCatching(
                tryBlock = {
                    val itemToUrl = mutableMapOf<ItemDto, String>()
                    supabaseImageStorage.list(ITEM_IMAGES_FOLDER_PATH) { limit = 500 }
                        .forEach { bucketItem ->
                            val itemName = bucketItem.name.removeSuffix(".png")
                            itemConstants.find { it.shortName == itemName }?.let { item ->
                                itemToUrl[item] =
                                    "${BuildConfig.STORAGE_ITEM_ICONS_FOLDER_URL}${bucketItem.name}"
                            }
                        }
                    itemToUrl.toMap()
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "getItemImageUrls failed")
                    throwable.wrapResultFailure()
                },
            )
        }

    override suspend fun getAbilityImageUrls(abilityConstants: List<AbilityDto>): Result<Map<AbilityDto, String>> =
        withContext(dispatchers.io) {
            coRunCatching(
                tryBlock = {
                    val abilityToUrl = mutableMapOf<AbilityDto, String>()
                    supabaseImageStorage.list(ABILITY_IMAGES_FOLDER_PATH) { limit = 2000 }
                        .forEach { bucketItem ->
                            val abilityName = bucketItem.name.removeSuffix(".png")
                            abilityConstants.find { it.name == abilityName }?.let { ability ->
                                abilityToUrl[ability] =
                                    "${BuildConfig.STORAGE_ABILITY_ICONS_FOLDER_URL}${bucketItem.name}"
                            }
                        }
                    abilityToUrl.toMap()
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "getAbilityImageUrls failed")
                    throwable.wrapResultFailure()
                },
            )
        }

    override suspend fun getAdditionalImageUrls(): Result<Map<String, String>> =
        withContext(dispatchers.io) {
            coRunCatching(
                tryBlock = {
                    val additionalToUrl = mutableMapOf<String, String>()
                    supabaseImageStorage.list(ADDITIONAL_IMAGES_FOLDER_PATH) { limit = 100 }
                        .forEach { bucketItem ->
                            val additionalName = bucketItem.name.removeSuffix(".png")
                            additionalToUrl[additionalName] =
                                "${BuildConfig.STORAGE_ADDITIONAL_ICONS_FOLDER_URL}${bucketItem.name}"
                        }
                    additionalToUrl.toMap()
                },
                catchBlock = { throwable ->
                    Napier.e(throwable = throwable, message = "getAdditionalImageUrls failed")
                    throwable.wrapResultFailure()
                },
            )
        }


    private companion object {
        private const val IMAGES_BUCKET_ID = "d2bh_images"
        private const val HERO_IMAGES_FOLDER_PATH = "hero_icons/"
        private const val ITEM_IMAGES_FOLDER_PATH = "item_icons/"
        private const val ABILITY_IMAGES_FOLDER_PATH = "ability_icons/"
        private const val ADDITIONAL_IMAGES_FOLDER_PATH = "additional_icons/"
    }

}

enum class AdditionalResourceNames(val title: String) {
    POSITION_1("POSITION_1"),
    POSITION_2("POSITION_2"),
    POSITION_3("POSITION_3"),
    POSITION_4("POSITION_4"),
    POSITION_5("POSITION_5"),
    RADIANT_SQUARE("radiant_square"),
    DIRE_SQUARE("dire_square"),
}
