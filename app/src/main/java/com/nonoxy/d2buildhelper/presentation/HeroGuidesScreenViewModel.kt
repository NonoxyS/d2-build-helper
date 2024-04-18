package com.nonoxy.d2buildhelper.presentation

import android.util.Log
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
class HeroGuidesScreenViewModel @Inject constructor(
    private val getGuidesInfoUseCase: GetGuidesInfoUseCase,
    private val getHeroGuidesInfoUseCase: GetHeroGuidesInfoUseCase,
    private val getHeroGuideBuildUseCase: GetHeroGuideBuildUseCase,
    private val getHeroImageUrlByNameUseCase: GetHeroImageUrlByNameUseCase,
    private val getItemImageUrlByNameUseCase: GetItemImageUrlByNameUseCase,
    private val getAdditionalImageUrlByNameUseCase: GetAdditionalImageUrlByNameUseCase,
    private val getHeroDetailsByIdUseCase: GetHeroDetailsByIdUseCase,
    private val getEachHeroDetailsUseCase: GetEachHeroDetailsUseCase,
    private val getItemNameByIdUseCase: GetItemNameByIdUseCase,
): ViewModel() {

    private val _buildsState = MutableStateFlow(BuildsState())
    private val _heroFilterState = MutableStateFlow(HeroFilterState())
    val buildsState = _buildsState.asStateFlow()
    val heroFilterState = _heroFilterState.asStateFlow()

    fun getHeroGuidesData(heroId: Short) {
        viewModelScope.launch {
            _buildsState.update { it.copy(
                isLoading = true
            ) }
            Log.d("TestTime", "start getting data")

            Log.d("TestTime", "start getting heroGuides from api")
            val heroGuides = async(Dispatchers.IO) {
                getHeroGuidesInfoUseCase.execute(heroId) }
            Log.d("TestTime", "end getting heroGuides from api")

            Log.d("TestTime", "start getting heroBuilds from api")
            val heroBuilds = async(Dispatchers.IO) { getHeroBuilds(heroGuides.await()) }
            Log.d("TestTime", "end getting heroBuilds from api")

            val itemPurchases = async { getItemPurchases(heroBuilds.await()) }

            val sortedBuildEndItemsByTime = async { sortEndItemsByTime(
                heroBuilds = heroBuilds.await(),
                itemPurchases = itemPurchases.await()) }

            val inventoryChanges = async { getInventoryChanges(heroBuilds.await()) }

            Log.d("TestTime", "start getting heroDetails from api")
            val heroDetails = getHeroDetailsByIdUseCase.execute(heroGuides.await().map { it.heroId.toInt() })
            Log.d("TestTime", "end getting heroDetails from api")

            Log.d("TestTime", "start getting heroImages from api")
            val heroImageUrls = async { getHeroImages(heroDetails) }
            Log.d("TestTime", "end getting heroImages from api")

            Log.d("TestTime", "start getting item images from api")
            val itemImageUrls = async { getItemImages(heroBuilds.await()) }
            Log.d("TestTime", "end getting item images from api")

            Log.d("TestTime", "start getting additional images from api")
            val additionalImageUrls = async { getAdditionalImages(heroBuilds.await()) }
            Log.d("TestTime", "end getting additional images from api")

            // Получаем имена для загрузки изображений позиции игрока и за какую сторону играл
            // Получаем имена предметов по их id для загрузки изображений
            // Получаем ссылки для загрузки изображений позиций и сторон (radiant/dire)
            // Получаем ссылки для загрузки изображений предметов
            // Ожидаем завершения всех асинхронных операций

            Log.d("TestTime", "end getting data")

            _buildsState.update { it.copy(
                heroGuides = heroGuides.await(),
                heroBuilds = heroBuilds.await(),
                itemImageUrls = itemImageUrls.await(),
                heroImageUrls = heroImageUrls.await(),
                heroDetails = heroDetails,
                additionalImageUrls = additionalImageUrls.await(),
                itemPurchases = itemPurchases.await(),
                inventoryChanges = inventoryChanges.await(),
                sortedBuildEndItemsByTime = sortedBuildEndItemsByTime.await(),
                isLoading = false
            ) }
            Log.d("TestTime", "GUIDES => ${heroGuides.await()}")
            Log.d("TestTime", "heroBuilds => ${heroBuilds.await()}")
            Log.d("TestTime", "itemImages => ${itemImageUrls.await()}")
            Log.d("TestTime", "heroImages => ${heroImageUrls.await()}")
            Log.d("TestTime", "heroDetails => $heroDetails")
            Log.d("TestTime", "additionalImage => ${additionalImageUrls.await()}")
            Log.d("TestTime", "itemPurchases => ${itemPurchases.await()}")
            Log.d("TestTime", "inventoryChanges => ${inventoryChanges.await()}")
            Log.d("TestTime", "sortedEndItems => ${sortedBuildEndItemsByTime.await()}")

            val eachHeroDetails = async { getEachHeroDetailsUseCase.execute() }
            val eachHeroImageUrls = async { getHeroImages(eachHeroDetails.await()) }

            _heroFilterState.update { it.copy(
                eachHeroDetails = eachHeroDetails.await(),
                eachHeroImageUrls = eachHeroImageUrls.await()
            ) }
            Log.d("TestTime", "after download get all data...")
        }
    }

    fun openHeroFilterDialog() {
        _heroFilterState.update { it.copy(
            expanded = true
        )
        }
    }

    fun dismissHeroFilterDialog() {
        _heroFilterState.update {it.copy(
            expanded = false
        )
        }
    }

    data class BuildsState(
        val heroGuides: List<HeroGuideInfo> = emptyList(),
        val heroBuilds: MutableMap<Short, List<HeroGuideBuild>> = mutableMapOf(),
        val itemImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroDetails: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val additionalImageUrls: MutableMap<String, String> = mutableMapOf(),
        val itemPurchases: MutableMap<Short, List<ItemPurchase>> = mutableMapOf(),
        val inventoryChanges: MutableMap<Short, List<InventoryChange>> = mutableMapOf(),
        val sortedBuildEndItemsByTime: MutableMap<Short, List<ItemPurchase>> = mutableMapOf(),
        val isLoading: Boolean = false,
    )

    data class HeroFilterState(
        val heroSelected: Short? = null,
        val eachHeroDetails: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val eachHeroImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val expanded: Boolean = false
    )

    private suspend fun getHeroBuilds(heroGuides: List<HeroGuideInfo>):
            MutableMap<Short, List<HeroGuideBuild>> {

        val heroBuilds = mutableMapOf<Short, List<HeroGuideBuild>>()

        if (heroGuides.isNotEmpty()) {
            val heroId = heroGuides.first().heroId
            val guidesInfo = heroGuides.first().guidesInfo ?: emptyList()

            coroutineScope {
                val buildsForHeroDeferred = guidesInfo.map { guideInfo ->
                    async(Dispatchers.IO) {
                        guideInfo?.let {
                            getHeroGuideBuildUseCase.execute(it.matchId, guideInfo.steamAccountId) }
                    }
                }

                val buildsForHero = buildsForHeroDeferred.awaitAll().filterNotNull()

                heroBuilds[heroId] = buildsForHero
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
                    val shortName = hero.value["shortName"]?: "null"
                    val imageUrl = getHeroImageUrlByNameUseCase.execute(shortName)
                    heroId to imageUrl
                }
            }.awaitAll().forEach { (heroId, imageUrl) ->
                heroImageUrls[heroId] = imageUrl
            }
        }
        return heroImageUrls
    }

    private suspend fun getItemImages(heroBuilds: MutableMap<Short, List<HeroGuideBuild>>):
            MutableMap<Short, String> {

        val itemImageUrls = mutableMapOf<Short, String>()
        coroutineScope {
            heroBuilds.values.first().forEach { build ->
                async {
                    val itemIds = listOfNotNull(
                        build.endItem0Id,
                        build.endItem1Id,
                        build.endItem2Id,
                        build.endItem3Id,
                        build.endItem4Id,
                        build.endItem5Id,
                        build.endBackpack0Id,
                        build.endBackpack1Id,
                        build.endBackpack2Id,
                        build.endNeutralItemId
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

    private suspend fun getAdditionalImages(heroBuilds: MutableMap<Short, List<HeroGuideBuild>>):
            MutableMap<String, String> {

        val additionalImageUrls = mutableMapOf<String, String>()
        coroutineScope {
            heroBuilds.values.first().forEach { build ->
                async {
                    val additionalNames = listOfNotNull(
                        build.position?.name,
                        if (build.isRadiant == true) "radiant_square" else "dire_square",
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

    private fun getItemPurchases(heroBuilds: MutableMap<Short, List<HeroGuideBuild>>):
            MutableMap<Short, List<ItemPurchase>> {

        val buildItemPurchases = mutableMapOf<Short, List<ItemPurchase>>()
        var i = 0
        heroBuilds.values.map { build ->
            buildItemPurchases[i.toShort()] =
                build[i++].itemPurchases?.mapNotNull { it }?: emptyList()
        }
        return buildItemPurchases
    }

    private fun getInventoryChanges(heroBuilds: MutableMap<Short, List<HeroGuideBuild>>):
            MutableMap<Short, List<InventoryChange>> {

        val buildInventoryChanges = mutableMapOf<Short, List<InventoryChange>>()
        heroBuilds.values.map { build ->
            val i = heroBuilds.values.indexOf(build)
            buildInventoryChanges[i.toShort()] =
                build[i].inventoryChanges?.mapNotNull { it }?: emptyList()
        }
        return buildInventoryChanges
    }

    private fun sortEndItemsByTime(heroBuilds: MutableMap<Short, List<HeroGuideBuild>>,
                                   itemPurchases: MutableMap<Short, List<ItemPurchase>>):
            MutableMap<Short, List<ItemPurchase>> {

        val sortedBuildEndItemsByTime = mutableMapOf<Short, List<ItemPurchase>>()
        heroBuilds.values.map { build ->
            val i = heroBuilds.values.indexOf(build)
            val endBuildItemIds = listOfNotNull(
                build[i].endItem0Id,
                build[i].endItem1Id,
                build[i].endItem2Id,
                build[i].endItem3Id,
                build[i].endItem4Id,
                build[i].endItem5Id)

            // Result is map of (heroId, List<ItemPurchase>) where itemPurchase contained in end
            // item build(next is EIB). First, list of itemPurchase filter by itemId contained in EIB
            // then sorting by unique itemId elements and get only item with last purchase time
            // After all, sorting the List<ItemPurchase> by time of buying item
            // P.S. i know, its pretty bad code and a bit unreadable code
            i.toShort() to itemPurchases[i.toShort()]?.filter { purchase ->
                purchase.itemId.toShort() in endBuildItemIds
            }?.groupBy { it.itemId.toShort() }
                ?.mapValues { (_, purchases) ->
                    purchases.maxBy { it.time }
                }?.values?.toList()?.sortedWith(compareBy { it.time })
        }.forEach { (buildNumber, sortedEndItems) ->
            sortedBuildEndItemsByTime[buildNumber] = sortedEndItems ?: emptyList()
        }
        return sortedBuildEndItemsByTime
    }
}