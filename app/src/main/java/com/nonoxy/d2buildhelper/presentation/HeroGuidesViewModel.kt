package com.nonoxy.d2buildhelper.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient
import com.nonoxy.d2buildhelper.domain.repository.ResourceClient
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo
import com.nonoxy.d2buildhelper.domain.usecases.GetGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuideBuildUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroGuidesInfoUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetHeroNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemImageUrlByNameUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetItemNameByIdUseCase
import com.nonoxy.d2buildhelper.domain.usecases.GetUtilImageUrlByNameUseCase
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
    private val getGuidesInfoUseCase: GetGuidesInfoUseCase,
    private val getHeroGuidesInfoUseCase: GetHeroGuidesInfoUseCase,
    private val getHeroGuideBuildUseCase: GetHeroGuideBuildUseCase,
    private val getHeroImageUrlByNameUseCase: GetHeroImageUrlByNameUseCase,
    private val getItemImageUrlByNameUseCase: GetItemImageUrlByNameUseCase,
    private val getUtilImageUrlByNameUseCase: GetUtilImageUrlByNameUseCase,
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
            val heroBuilds: MutableMap<Short, List<HeroGuideBuild>> = mutableMapOf()
            val itemImageUrls: MutableMap<Short, String> = mutableMapOf()
            val heroImageUrls: MutableMap<Short, String> = mutableMapOf()
            val heroNames: MutableMap<Short, MutableMap<String, String>> = mutableMapOf()
            val utilImagesUrls: MutableMap<String, String> = mutableMapOf()
            Log.d("FBImage", "guides before heroIds => $guides")
            val heroIds = guides.map { it.heroId.toInt() }
            //Log.d("FBImage", "heroIds => $heroIds")
            Log.d("TestTime", "start getting heroNames from api")
            val heroNamesResults = getHeroNameByIdUseCase.execute(heroIds)
            Log.d("TestTime", "end getting heroNames from api")
            val fetchImageUrlsJobs = mutableListOf<Deferred<Unit>>()

            Log.d("FBImage", "heroNames => $heroNamesResults")
            Log.d("TestTime", "start getting heroImages from api")
            heroNamesResults.forEach { (heroId, heroNamesMap) ->
                heroNames[heroId] = heroNamesMap
                fetchImageUrlsJobs += async {
                    heroImageUrls[heroId] = getHeroImageUrlByNameUseCase.execute(
                            "${heroNamesMap["shortName"]}")
                }
            }
            Log.d("TestTime", "end getting heroImages from api")
            Log.d("TestTime", "start getting heroBuild for guide from api")
            guides.forEach { guide ->
                // Получаем первый элемент guidesInfo или null, если он пуст
                val firstGuideInfo = guide.guidesInfo?.firstOrNull()

                // Если firstGuideInfo не null, выполняем запрос асинхронно
                val build = firstGuideInfo?.let { info ->
                    async {
                        getHeroGuideBuildUseCase.execute(
                            matchId = info.matchId, steamAccountId = info.steamAccountId)
                    }
                }?.await() // Ожидаем результат асинхронной операции

                // Добавляем результат в heroBuilds, если он не null, иначе добавляем пустой список
                heroBuilds[guide.heroId] = build?.let { listOf(it) } ?: mutableListOf()
            }
            Log.d("TestTime", "end getting heroBuild for guide from api")
            Log.d("TestTime", "start getting util and item images for build from api")
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
                        val itemIdNameMap = getItemNameByIdUseCase.execute(itemIds.map { it.toInt() })

                        // Получаем ссылки для загрузки изображений позиций и сторон (radiant/dire)
                        utilNames.forEach { utilName ->
                            utilImagesUrls[utilName] = getUtilImageUrlByNameUseCase.execute(utilName)
                        }

                        // Получаем ссылки для загрузки изображений предметов
                        itemIdNameMap.forEach { (key, value) ->
                            itemImageUrls[key] = getItemImageUrlByNameUseCase.execute(value)
                        }
                    }
                }
            }
            Log.d("TestTime", "end getting util and item images for build from api")
            // Ожидаем завершения всех асинхронных операций
            fetchImageUrlsJobs.awaitAll()
            Log.d("TestTime", "end getting data")
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