package dev.nonoxy.d2buildhelper.features.guides.domain

import androidx.compose.ui.util.fastLastOrNull
import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStats
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.GuideUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.ItemPurchaseUI
import dev.nonoxy.d2buildhelper.features.guides.domain.models.MatchPlayerPositionType
import dev.nonoxy.d2buildhelper.features.guides.domain.models.PlayerStatsUI
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class GetGuidesAndImagesUseCase(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository
) {
    internal operator fun invoke(): Flow<GuidesViewState> {
        return combine(
            guidesRepository.getGuides(),
            resourcesRepository.getHeroImages(),
            resourcesRepository.getItemImages(),
            resourcesRepository.getAdditionalImages()
        ) { guidesResult, heroImagesResult, itemImagesResult, additionalImagesResult ->
            when {
                guidesResult is RequestResult.Error ||
                        heroImagesResult is RequestResult.Error ||
                        itemImagesResult is RequestResult.Error ||
                        additionalImagesResult is RequestResult.Error -> GuidesViewState.Error

                guidesResult is RequestResult.InProgress ||
                        heroImagesResult is RequestResult.InProgress ||
                        itemImagesResult is RequestResult.InProgress ||
                        additionalImagesResult is RequestResult.InProgress -> GuidesViewState.Loading

                else -> {
                    val guides = (guidesResult as RequestResult.Success).data.map { it.toGuideUi() }
                    val heroImages = (heroImagesResult as RequestResult.Success).data
                    val itemImages = (itemImagesResult as RequestResult.Success).data
                    val additionalImages = (additionalImagesResult as RequestResult.Success).data

                    val imageResources =
                        ImageResources(
                            heroImages = heroImages.mapKeys { it.key.id },
                            itemImages = itemImages.mapKeys { it.key.id },
                            abilityImages = emptyMap(),
                            additionalImages = additionalImages
                        )
                    GuidesViewState.Display(guides = guides, imageResources = imageResources)
                }
            }
        }.flowOn(Dispatchers.Default)
            .catch { emit(GuidesViewState.Error) }
    }
}

internal fun Guide.toGuideUi(): GuideUI {
    return GuideUI(
        hero = HeroUI(
            heroId = hero.heroId,
            shortName = hero.shortName,
            displayName = hero.displayName
        ),
        steamAccountId = steamAccountId,
        matchId = matchId,
        durationSeconds = durationSeconds,
        playerStats = playerStats.toPlayerStatsUi()
    )
}

internal fun PlayerStats.toPlayerStatsUi(): PlayerStatsUI {
    val endItemIds =
        listOfNotNull(endItem0Id, endItem1Id, endItem2Id, endItem3Id, endItem4Id, endItem5Id)
    // Ищем itemPurchase для каждого предмета в финальной сборке.
    // Т.к. в АПИ могут быть предметы в финальной сборке, но не отображаться в списке itemPurchases,
    // то мы просто добавляем недостающие элементы со значением time = null и сортируем
    // полученный список по времени, все элементы с time = null будут располагаться в конце.
    val sortedEndItemPurchases = endItemIds.map { endItemId ->
        itemPurchases?.fastLastOrNull { it?.itemId?.toShort() == endItemId }?.let { itemPurchase ->
            ItemPurchaseUI(itemId = itemPurchase.itemId.toShort(), time = itemPurchase.time)
        } ?: ItemPurchaseUI(itemId = endItemId, time = null)
    }.sortedWith(compareBy(nullsLast()) { it.time })

    return PlayerStatsUI(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant ?: true,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = impact ?: 25,
        endNeutralItemId = endNeutralItemId,
        sortedEndItemPurchases = sortedEndItemPurchases
    )
}