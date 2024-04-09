package com.nonoxy.d2buildhelper.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.usecases.GetGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuideBuildUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetAdditionalImageUrlByNameUseCase
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
class HeroGuidesViewModel @Inject constructor(
    private val getGuidesInfoUseCase: GetGuidesInfoUseCase,
    private val getHeroGuidesInfoUseCase: GetHeroGuidesInfoUseCase,
    private val getHeroGuideBuildUseCase: GetHeroGuideBuildUseCase,
    private val getHeroImageUrlByNameUseCase: GetHeroImageUrlByNameUseCase,
    private val getItemImageUrlByNameUseCase: GetItemImageUrlByNameUseCase,
    private val getAdditionalImageUrlByNameUseCase: GetAdditionalImageUrlByNameUseCase,
    private val getHeroNameByIdUseCase: GetHeroNameByIdUseCase,
    private val getItemNameByIdUseCase: GetItemNameByIdUseCase,
): ViewModel() {

    private val _state = MutableStateFlow(BuildsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }
            Log.d("TestTime", "start getting data")

            Log.d("TestTime", "start getting guides from api")
            val guides = getGuidesInfoUseCase.execute()
            Log.d("TestTime", "end getting guides from api")

            Log.d("TestTime", "start getting heroBuilds from api")
            val heroBuilds = getHeroBuilds(guides)
            Log.d("TestTime", "end getting heroBuilds from api")

            Log.d("TestTime", "start getting heroNames from api")
            val heroNames = getHeroNameByIdUseCase.execute(guides.map { it.heroId.toInt() })
            Log.d("TestTime", "end getting heroNames from api")

            Log.d("TestTime", "start getting heroImages from api")
            val heroImageUrls = getHeroImages(heroNames)
            Log.d("TestTime", "end getting heroImages from api")

            Log.d("TestTime", "start getting item images from api")
            val itemImageUrls = getItemImages(heroBuilds)
            Log.d("TestTime", "end getting item images from api")

            Log.d("TestTime", "start getting additional images from api")
            val additionalImageUrls = getAdditionalImages(heroBuilds)
            Log.d("TestTime", "end getting additional images from api")

            // Получаем имена для загрузки изображений позиции игрока и за какую сторону играл
            // Получаем имена предметов по их id для загрузки изображений
            // Получаем ссылки для загрузки изображений позиций и сторон (radiant/dire)
            // Получаем ссылки для загрузки изображений предметов
            // Ожидаем завершения всех асинхронных операций

            Log.d("TestTime", "end getting data")

            Log.d("TestTime", "GUIDES => $guides")
            Log.d("TestTime", "heroBuilds => $heroBuilds")
            Log.d("TestTime", "itemImages => $itemImageUrls")
            Log.d("TestTime", "heroImages => $heroImageUrls")
            Log.d("TestTime", "heroNames => $heroNames")
            Log.d("TestTime", "additionalImage => $additionalImageUrls")
            _state.update { it.copy(
                guides = guides,
                heroBuilds = heroBuilds,
                itemImageUrls = itemImageUrls,
                heroImageUrls = heroImageUrls,
                heroNames = heroNames,
                additionalImageUrls = additionalImageUrls,
                isLoading = false
            ) }
        }
    }

    data class BuildsState(
        val guides: List<HeroGuideInfo> = emptyList(),
        val heroBuilds: MutableMap<Short, HeroGuideBuild> = mutableMapOf(),
        val itemImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroNames: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val additionalImageUrls: MutableMap<String, String> = mutableMapOf(),
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

    private suspend fun getHeroImages(heroNames: MutableMap<Short, MutableMap<String, String>>):
            MutableMap<Short, String> {

        val heroImageUrls = mutableMapOf<Short, String>()
        coroutineScope {
            heroNames.map { hero ->
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

}