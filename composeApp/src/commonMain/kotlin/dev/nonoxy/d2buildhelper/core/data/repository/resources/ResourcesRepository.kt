package dev.nonoxy.d2buildhelper.core.data.repository.resources

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.ImageResourcesApi
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.ConstantResources
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Ability
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Hero
import dev.nonoxy.d2buildhelper.core.data.local.resources.constants.models.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ResourcesRepository(
    private val imageResourcesDataSource: ImageResourcesApi,
    private val constantResourcesDataSource: ConstantResources
) {
    private var heroList: List<Hero>? = null
    private var itemList: List<Item>? = null
    private var abilityList: List<Ability>? = null

    private var heroImageUrls: Map<Hero, String>? = null
    private var itemImageUrls: Map<Item, String>? = null
    private var abilityImageUrls: Map<Ability, String>? = null
    private var additionalImageUrls: Map<String, String>? = null

    private suspend fun fetchAllConstantsDataIfNeeded() {
        if (heroList == null) {
            constantResourcesDataSource.getHeroConstants().collect { heroes ->
                heroList = heroes
            }
        }
        if (itemList == null) {
            constantResourcesDataSource.getItemConstants().collect { items ->
                itemList = items
            }
        }
        if (abilityList == null) {
            constantResourcesDataSource.getAbilityConstants().collect { abilities ->
                abilityList = abilities
            }
        }
    }

    fun getHeroImages(): Flow<RequestResult<Map<Hero, String>>> {
        return flow {
            emit(RequestResult.InProgress())

            // Если есть закешированные данные - выводим их
            heroImageUrls?.let { cachedHeroImageUrls ->
                emit(RequestResult.Success(cachedHeroImageUrls))
                return@flow
            }

            // Загрузка констант, если они ещё не загружены
            fetchAllConstantsDataIfNeeded()

            heroList?.let { heroList ->
                imageResourcesDataSource.getHeroImageUrls(heroList).collect { result ->
                    if (result is RequestResult.Success) {
                        heroImageUrls = result.data
                    }
                    emit(result)
                }
            }
        }.catch { e -> emit(RequestResult.Error(e)) }
    }

    fun getItemImages(): Flow<RequestResult<Map<Item, String>>> {
        return flow {
            emit(RequestResult.InProgress())

            // Если есть закешированные данные - выводим их
            itemImageUrls?.let { cachedItemImageUrls ->
                emit(RequestResult.Success(cachedItemImageUrls))
                return@flow
            }

            // Загрузка констант, если они ещё не загружены
            fetchAllConstantsDataIfNeeded()


            itemList?.let { itemList ->
                imageResourcesDataSource.getItemImageUrls(itemList).collect { result ->
                    if (result is RequestResult.Success) {
                        itemImageUrls = result.data
                    }
                    emit(result)
                }
            }
        }.catch { e -> emit(RequestResult.Error(e)) }
    }

    fun getAbilityImages(): Flow<RequestResult<Map<Ability, String>>> {
        return flow {
            emit(RequestResult.InProgress())

            // Если есть закешированные данные - выводим их
            abilityImageUrls?.let { cachedAbilityImageUrls ->
                emit(RequestResult.Success(cachedAbilityImageUrls))
                return@flow
            }

            // Загрузка констант, если они ещё не загружены
            fetchAllConstantsDataIfNeeded()


            abilityList?.let { abilityList ->
                imageResourcesDataSource.getAbilityImageUrls(abilityList).collect { result ->
                    if (result is RequestResult.Success) {
                        abilityImageUrls = result.data
                    }
                    emit(result)
                }
            }
        }.catch { e -> emit(RequestResult.Error(e)) }
    }

    fun getAdditionalImages(): Flow<RequestResult<Map<String, String>>> {
        return flow {
            emit(RequestResult.InProgress())

            // Если есть закешированные данные - выводим их
            additionalImageUrls?.let { cachedAdditionalImageUrls ->
                emit(RequestResult.Success(cachedAdditionalImageUrls))
                return@flow
            }

            // Загрузка констант, если они ещё не загружены
            fetchAllConstantsDataIfNeeded()

            imageResourcesDataSource.getAdditionalImageUrls().collect { result ->
                if (result is RequestResult.Success) {
                    additionalImageUrls = result.data
                }
                emit(result)
            }
        }.catch { e -> emit(RequestResult.Error(e)) }
    }
}