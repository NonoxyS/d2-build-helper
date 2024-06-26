package dev.nonoxy.d2buildhelper.core.data.api.resources.image

import Dota___Build_Helper.composeApp.BuildConfig
import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.withContext

class ImageResourcesDataSource(
    private val supabaseClient: SupabaseClient = InjectProvider.getDependency(SupabaseClient::class)
) : ImageResourcesApi {
    private val supabaseImageStorage = supabaseClient.storage.from(IMAGES_BUCKET_ID)

    override fun getHeroImageUrls(heroConstants: List<Hero>): Flow<RequestResult<Map<Hero, String>>> {
        val heroToUrl: MutableMap<Hero, String> = mutableMapOf()
        val request = flow {
            supabaseImageStorage.list(HERO_IMAGES_FOLDER_PATH) { limit = 200 }
                .onEach { bucketItem ->
                    withContext(Dispatchers.Default) {
                        val heroName = bucketItem.name.removeSuffix("_minimap_icon.png")
                        heroConstants.find { hero ->
                            hero.shortName == heroName
                        }?.let { hero ->
                            heroToUrl[hero] =
                                "${BuildConfig.STORAGE_HERO_ICONS_FOLDER_URL}${bucketItem.name}"
                        }
                    }
                }
            emit(heroToUrl.toMap())
        }.flowOn(Dispatchers.IO)
            .map { RequestResult.Success(it) }
            .catch { e -> RequestResult.Error<Map<Hero, String>>(e) }

        val start = flowOf<RequestResult<Map<Hero, String>>>(RequestResult.InProgress())

        return merge(request, start)
    }

    override fun getItemImageUrls(itemConstants: List<Item>): Flow<RequestResult<Map<Item, String>>> {
        val itemToUrl: MutableMap<Item, String> = mutableMapOf()
        val request = flow {
            supabaseImageStorage.list(ITEM_IMAGES_FOLDER_PATH) { limit = 500 }
                .onEach { bucketItem ->
                    withContext(Dispatchers.Default) {
                        val itemName = bucketItem.name.removeSuffix(".png")
                        itemConstants.find { item ->
                            item.shortName == itemName
                        }?.let { item ->
                            itemToUrl[item] =
                                "${BuildConfig.STORAGE_ITEM_ICONS_FOLDER_URL}${bucketItem.name}"
                        }
                    }
                }
            emit(itemToUrl.toMap())
        }.flowOn(Dispatchers.IO)
            .map { RequestResult.Success(it) }
            .catch { e -> RequestResult.Error<Map<Item, String>>(e) }

        val start = flowOf<RequestResult<Map<Item, String>>>(RequestResult.InProgress())

        return merge(request, start)
    }

    override fun getAbilityImageUrls(abilityConstants: List<Ability>): Flow<RequestResult<Map<Ability, String>>> {
        val abilityToUrl: MutableMap<Ability, String> = mutableMapOf()
        val request = flow {
            supabaseImageStorage.list(ABILITY_IMAGES_FOLDER_PATH) { limit = 2000 }
                .onEach { bucketItem ->
                    withContext(Dispatchers.Default) {
                        val abilityName = bucketItem.name.removeSuffix(".png")
                        abilityConstants.find { ability ->
                            ability.name == abilityName
                        }?.let { ability ->
                            abilityToUrl[ability] =
                                "${BuildConfig.STORAGE_ABILITY_ICONS_FOLDER_URL}${bucketItem.name}"
                        }
                    }
                }
            emit(abilityToUrl.toMap())
        }.flowOn(Dispatchers.IO)
            .map { RequestResult.Success(it) }
            .catch { e -> RequestResult.Error<Map<Ability, String>>(e) }

        val start = flowOf<RequestResult<Map<Ability, String>>>(RequestResult.InProgress())

        return merge(request, start)
    }

    override fun getAdditionalImageUrls(): Flow<RequestResult<Map<String, String>>> {
        val additionalToUrl: MutableMap<String, String> = mutableMapOf()
        val request = flow {
            supabaseImageStorage.list(ADDITIONAL_IMAGES_FOLDER_PATH) { limit = 100 }
                .onEach { bucketItem ->
                    val additionalName = bucketItem.name.removeSuffix(".png")
                    additionalToUrl[additionalName] =
                        "${BuildConfig.STORAGE_ADDITIONAL_ICONS_FOLDER_URL}${bucketItem.name}"
                }
            emit(additionalToUrl.toMap())
        }.flowOn(Dispatchers.IO)
            .map { RequestResult.Success(it) }
            .catch { e -> RequestResult.Error<Map<String, String>>(e) }

        val start = flowOf<RequestResult<Map<String, String>>>(RequestResult.InProgress())

        return merge(request, start)
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
