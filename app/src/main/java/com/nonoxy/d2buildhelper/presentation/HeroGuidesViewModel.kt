package com.nonoxy.d2buildhelper.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nonoxy.d2buildhelper.domain.HeroBuildClient
import com.nonoxy.d2buildhelper.domain.ResourceClient
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeroGuidesViewModel @Inject constructor(
    private val getHeroBuild: HeroBuildClient,
    private val getDotaResources: ResourceClient): ViewModel() {

    private val _state = MutableStateFlow(BuildsState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true
            ) }

            val guides = getHeroBuild.getGuidesInfo()
            val heroBuilds: MutableMap<Short, List<HeroGuideBuild>> = mutableMapOf()
            val itemImageUrls: MutableMap<Short, String> = mutableMapOf()
            val heroImageUrls: MutableMap<Short, String> = mutableMapOf()
            val heroNames: MutableMap<Short, MutableMap<String, String>> = mutableMapOf()
            val utilImagesUrls: MutableMap<String, String> = mutableMapOf()
            Log.d("FBImage", "guides before heroIds => $guides")
            val heroIds = guides.map { it.heroId.toInt() }
            Log.d("FBImage", "heroIds => $heroIds")
            val heroNamesResults = getDotaResources.getHeroNameById(heroIds)

            val fetchImageUrlsJobs = mutableListOf<Deferred<Unit>>()

            Log.d("FBImage", "heroNames => $heroNamesResults")
            heroNamesResults.forEach { (heroId, heroNamesMap) ->
                heroNames[heroId] = heroNamesMap
                fetchImageUrlsJobs += async {
                    heroImageUrls[heroId] = getDotaResources.getHeroImageUrlByName(
                            "${heroNamesMap["shortName"]}")
                }
            }

            guides.forEach { guide ->
                // Получаем первый элемент guidesInfo или null, если он пуст
                val firstGuideInfo = guide.guidesInfo?.firstOrNull()

                // Если firstGuideInfo не null, выполняем запрос асинхронно
                val build = firstGuideInfo?.let { info ->
                    async {
                        getHeroBuild.getHeroGuideBuild(info.matchId, info.steamAccountId)
                    }
                }?.await() // Ожидаем результат асинхронной операции

                // Добавляем результат в heroBuilds, если он не null, иначе добавляем пустой список
                heroBuilds[guide.heroId] = build?.let { listOf(it) } ?: mutableListOf()
            }

            heroBuilds.forEach { (_, builds) ->
                builds.forEach { build ->
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
                    // Получаем имена для загрузки изображений позиции игрока и за какую сторону играл
                    val utilNames = listOfNotNull(
                        build.position?.name,
                        if (build.isRadiant == true) "radiant_square" else "dire_square",
                    )

                    fetchImageUrlsJobs += async {
                        // Получаем имена предметов по их id для загрузки изображений
                        val itemIdNameMap = getDotaResources.getItemNameById(itemIds.map { it.toInt() })

                        // Получаем ссылки для загрузки изображений позиций и сторон (radiant/dire)
                        utilNames.forEach { utilName ->
                            utilImagesUrls[utilName] = getDotaResources.getUtilImageUrlByName(utilName)
                        }

                        // Получаем ссылки для загрузки изображений предметов
                        itemIdNameMap.forEach { (key, value) ->
                            itemImageUrls[key] = getDotaResources.getItemImageUrlByName(value)
                        }
                    }
                }
            }
            // Ожидаем завершения всех асинхронных операций
            fetchImageUrlsJobs.awaitAll()

            _state.update { it.copy(
                guides = guides,
                heroBuilds = heroBuilds,
                itemImageUrls = itemImageUrls,
                heroImageUrls = heroImageUrls,
                heroNames = heroNames,
                utilImagesUrls = utilImagesUrls,
                isLoading = false
            ) }
        }
    }

    data class BuildsState(
        val guides: List<HeroGuideInfo> = emptyList(),
        val heroBuilds: MutableMap<Short, List<HeroGuideBuild>> = mutableMapOf(),
        val itemImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroImageUrls: MutableMap<Short, String> = mutableMapOf(),
        val heroNames: MutableMap<Short, MutableMap<String, String>> = mutableMapOf(),
        val utilImagesUrls: MutableMap<String, String> = mutableMapOf(),
        val isLoading: Boolean = false,
    )
}