package dev.nonoxy.d2buildhelper.features.guides.domain

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.Guide
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.PlayerStats
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.domain.models.*
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
        hero = Hero(
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
    val endItemIds = listOfNotNull(endItem0Id, endItem1Id, endItem2Id, endItem3Id, endItem4Id, endItem5Id)

    return PlayerStatsUI(
        position = MatchPlayerPositionType.valueOf(position?.name ?: "UNKNOWN"),
        isRadiant = isRadiant ?: true,
        kills = kills,
        deaths = deaths,
        assists = assists,
        impact = impact ?: 25,
        endItem0Id = endItem0Id,
        endItem1Id = endItem1Id,
        endItem2Id = endItem2Id,
        endItem3Id = endItem3Id,
        endItem4Id = endItem4Id,
        endItem5Id = endItem5Id,
        endBackpack0Id = endBackpack0Id,
        endBackpack1Id = endBackpack1Id,
        endBackpack2Id = endBackpack2Id,
        endNeutralItemId = endNeutralItemId,
        sortedEndItemPurchases = itemPurchases?.mapNotNull { itemPurchase ->
            itemPurchase?.let { ItemPurchase(itemId = it.itemId, time = itemPurchase.time) }
        } ?: emptyList<ItemPurchase>().filter { itemPurchase ->
            itemPurchase.itemId.toShort() in endItemIds
        }.sortedBy { itemPurchase ->
            itemPurchase.time
        }
    )
}