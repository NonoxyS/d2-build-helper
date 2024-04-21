package com.nonoxy.d2buildhelper.presentation.guides

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.model.InventoryChange
import com.nonoxy.d2buildhelper.domain.model.ItemPurchase
import com.nonoxy.d2buildhelper.domain.usecases.GetAdditionalImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetEachHeroDetailsUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroDetailsByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuideBuildUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemNameByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuidesScreenViewModel @Inject constructor(
    private val getGuidesInfoUseCase: GetGuidesInfoUseCase,
    private val getHeroGuidesInfoUseCase: GetHeroGuidesInfoUseCase,
    private val getHeroGuideBuildUseCase: GetHeroGuideBuildUseCase,
    private val getHeroImageUrlByNameUseCase: GetHeroImageUrlByNameUseCase,
    private val getItemImageUrlByNameUseCase: GetItemImageUrlByNameUseCase,
    private val getAdditionalImageUrlByNameUseCase: GetAdditionalImageUrlByNameUseCase,
    private val getHeroDetailsByIdUseCase: GetHeroDetailsByIdUseCase,
    private val getEachHeroDetailsUseCase: GetEachHeroDetailsUseCase,
    private val getItemNameByIdUseCase: GetItemNameByIdUseCase,
) : ViewModel() {

    private val _buildsState = MutableStateFlow(BuildsState())
    val buildsState = _buildsState.asStateFlow()

    init {
        getGuidesData()
    }

    fun getGuidesData() {
        viewModelScope.launch {
            _buildsState.update {
                it.copy(
                    isLoading = true
                )
            }
            Log.d("TestTime", "start getting data")

            Log.d("TestTime", "start getting guides from api")
            val guides = async(Dispatchers.IO) { getGuidesInfoUseCase.execute() }
            Log.d("TestTime", "end getting guides from api")

            Log.d("TestTime", "start getting heroBuilds from api")
            val heroBuilds = async(Dispatchers.IO) { getHeroBuilds(guides.await()) }
            Log.d("TestTime", "end getting heroBuilds from api")

            val itemPurchases = async { getItemPurchases(heroBuilds.await()) }

            val sortedBuildEndItemsByTime = async {
                sortEndItemsByTime(
                    heroBuilds = heroBuilds.await(),
                    itemPurchases = itemPurchases.await()
                )
            }

            val inventoryChanges = async { getInventoryChanges(heroBuilds.await()) }

            Log.d("TestTime", "start getting heroDetails from api")
            val heroDetails = async(Dispatchers.IO) {
                getHeroDetailsByIdUseCase.execute(
                    guides.await().map { it.heroId.toInt() })
            }
            Log.d("TestTime", "end getting heroDetails from api")

            Log.d("TestTime", "start getting heroImages from api")
            val heroImageUrls = async { getHeroImages(heroDetails.await()) }
            Log.d("TestTime", "end getting heroImages from api")

            Log.d("TestTime", "start getting item images from api")
            val itemImageUrls = async { getItemImages(heroBuilds.await()) }
            Log.d("TestTime", "end getting item images from api")

            Log.d("TestTime", "start getting additional images from api")
            val additionalImageUrls = async { getAdditionalImages(heroBuilds.await()) }
            Log.d("TestTime", "end getting additional images from api")

            Log.d("TestTime", "end getting data")

            _buildsState.update {
                it.copy(
                    guides = guides.await(),
                    heroBuilds = heroBuilds.await(),
                    itemImageUrls = itemImageUrls.await(),
                    heroImageUrls = heroImageUrls.await(),
                    heroDetails = heroDetails.await(),
                    additionalImageUrls = additionalImageUrls.await(),
                    itemPurchases = itemPurchases.await(),
                    inventoryChanges = inventoryChanges.await(),
                    sortedBuildEndItemsByTime = sortedBuildEndItemsByTime.await(),
                    isLoading = false
                )
            }
            Log.d("TestTime", "GUIDES => ${guides.await()}")
            Log.d("TestTime", "heroBuilds => ${heroBuilds.await()}")
            Log.d("TestTime", "itemImages => ${itemImageUrls.await()}")
            Log.d("TestTime", "heroImages => ${heroImageUrls.await()}")
            Log.d("TestTime", "heroDetails => $heroDetails")
            Log.d("TestTime", "additionalImage => ${additionalImageUrls.await()}")
            Log.d("TestTime", "itemPurchases => ${itemPurchases.await()}")
            Log.d("TestTime", "inventoryChanges => ${inventoryChanges.await()}")
            Log.d("TestTime", "sortedEndItems => ${sortedBuildEndItemsByTime.await()}")

            Log.d("TestTime", "after download get all data...")
        }
    }

    @Stable
    data class BuildsState(
        val guides: List<HeroGuideInfo> = emptyList(),
        val heroBuilds: MutableMap<Short, HeroGuideBuild> = mutableMapOf(),
        val itemImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroDetails: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val additionalImageUrls: MutableMap<String, String> = mutableMapOf(),
        val itemPurchases: MutableMap<Short, List<ItemPurchase>> = mutableMapOf(),
        val inventoryChanges: MutableMap<Short, List<InventoryChange>> = mutableMapOf(),
        val sortedBuildEndItemsByTime: MutableMap<Short, List<ItemPurchase>> = mutableMapOf(),
        val isLoading: Boolean = false,
    )

    private suspend fun getHeroBuilds(guides: List<HeroGuideInfo>):
            MutableMap<Short, HeroGuideBuild> {

        val heroBuilds = mutableMapOf<Short, HeroGuideBuild>()
        coroutineScope {
            guides.map { guide ->
                async(Dispatchers.IO) {
                    val firstGuideInfo = guide.guidesInfo?.firstOrNull()
                    val heroId = guide.heroId
                    val build = firstGuideInfo?.let { guideInfo ->
                        getHeroGuideBuildUseCase.execute(
                            guideInfo.matchId,
                            guideInfo.steamAccountId
                        )
                    }
                    heroId to build
                }
            }.awaitAll().forEach { (heroId, build) ->
                if (build != null) heroBuilds[heroId] = build
            }
        }
        return heroBuilds
    }

    private suspend fun getHeroImages(heroDetails: MutableMap<Short, MutableMap<String, String>>):
            MutableMap<Short, String> {

        val heroImageUrls = mutableMapOf<Short, String>()
        coroutineScope {
            heroDetails.map { hero ->
                async {
                    val heroId = hero.key
                    val shortName = hero.value["shortName"] ?: "null"
                    val imageUrl = getHeroImageUrlByNameUseCase.execute(shortName)
                    heroId to imageUrl
                }
            }.awaitAll().forEach { (heroId, imageUrl) ->
                heroImageUrls[heroId] = imageUrl
            }
        }
        return heroImageUrls
    }

    private suspend fun getItemImages(heroBuilds: MutableMap<Short, HeroGuideBuild>):
            MutableMap<Short, String> {

        val itemImageUrls = mutableMapOf<Short, String>()
        coroutineScope {
            heroBuilds.forEach { build ->
                async {
                    val itemIds = listOfNotNull(
                        build.value.endItem0Id,
                        build.value.endItem1Id,
                        build.value.endItem2Id,
                        build.value.endItem3Id,
                        build.value.endItem4Id,
                        build.value.endItem5Id,
                        build.value.endBackpack0Id,
                        build.value.endBackpack1Id,
                        build.value.endBackpack2Id,
                        build.value.endNeutralItemId
                    )

                    val itemNames = getItemNameByIdUseCase.execute(itemIds.map { it.toInt() })

                    itemNames.map { (itemId, itemName) ->
                        async {
                            val id = itemId
                            val url = getItemImageUrlByNameUseCase.execute(itemName)
                            id to url
                        }
                    }.awaitAll().forEach { (itemId, itemUrl) ->
                        itemImageUrls[itemId] = itemUrl
                    }
                }.await()
            }
        }
        return itemImageUrls
    }

    private suspend fun getAdditionalImages(heroBuilds: MutableMap<Short, HeroGuideBuild>):
            MutableMap<String, String> {

        val additionalImageUrls = mutableMapOf<String, String>()
        coroutineScope {
            heroBuilds.forEach { build ->
                async {
                    val additionalNames = listOfNotNull(
                        build.value.position?.name,
                        if (build.value.isRadiant == true) "radiant_square" else "dire_square",
                    )
                    additionalNames.map { additionalName ->
                        async {
                            val url = getAdditionalImageUrlByNameUseCase.execute(additionalName)
                            additionalName to url
                        }
                    }.awaitAll().forEach { (additionalName, url) ->
                        additionalImageUrls[additionalName] = url
                    }
                }.await()
            }
        }
        return additionalImageUrls
    }

    private fun getItemPurchases(heroBuilds: MutableMap<Short, HeroGuideBuild>):
            MutableMap<Short, List<ItemPurchase>> {

        val buildItemPurchases = mutableMapOf<Short, List<ItemPurchase>>()
        heroBuilds.map { build ->
            buildItemPurchases[build.key] =
                build.value.itemPurchases?.mapNotNull { it } ?: emptyList()
        }
        return buildItemPurchases
    }

    private fun getInventoryChanges(heroBuilds: MutableMap<Short, HeroGuideBuild>):
            MutableMap<Short, List<InventoryChange>> {

        val buildInventoryChanges = mutableMapOf<Short, List<InventoryChange>>()
        heroBuilds.map { build ->
            buildInventoryChanges[build.key] =
                build.value.inventoryChanges?.mapNotNull { it } ?: emptyList()
        }
        return buildInventoryChanges
    }

    private fun sortEndItemsByTime(
        heroBuilds: MutableMap<Short, HeroGuideBuild>,
        itemPurchases: MutableMap<Short, List<ItemPurchase>>
    ):
            MutableMap<Short, List<ItemPurchase>> {

        val sortedBuildEndItemsByTime = mutableMapOf<Short, List<ItemPurchase>>()
        heroBuilds.map { (heroId, build) ->
            val endBuildItemIds = listOfNotNull(
                build.endItem0Id,
                build.endItem1Id,
                build.endItem2Id,
                build.endItem3Id,
                build.endItem4Id,
                build.endItem5Id
            )

            // Result is map of (heroId, List<ItemPurchase>) where itemPurchase contained in end
            // item build(next is EIB). First, list of itemPurchase filter by itemId contained in EIB
            // then sorting by unique itemId elements and get only item with last purchase time
            // After all, sorting the List<ItemPurchase> by time of buying item
            // P.S. i know, its pretty bad code and a bit unreadable code
            heroId to itemPurchases[heroId]?.filter { purchase ->
                purchase.itemId.toShort() in endBuildItemIds
            }?.groupBy { it.itemId.toShort() }
                ?.mapValues { (_, purchases) ->
                    purchases.maxBy { it.time }
                }?.values?.toList()?.sortedWith(compareBy { it.time })
        }.forEach { (heroId, sortedEndItems) ->
            sortedBuildEndItemsByTime[heroId] = sortedEndItems ?: emptyList()
        }
        return sortedBuildEndItemsByTime
    }
}