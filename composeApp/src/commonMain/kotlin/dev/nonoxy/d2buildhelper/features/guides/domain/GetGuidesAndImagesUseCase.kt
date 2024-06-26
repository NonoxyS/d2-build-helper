package dev.nonoxy.d2buildhelper.features.guides.domain

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.core.data.repository.guides.GuidesRepository
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.features.guides.presentation.models.GuidesViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine

class GetGuidesAndImagesUseCase(
    private val guidesRepository: GuidesRepository,
    private val resourcesRepository: ResourcesRepository
) {
    internal operator fun invoke(): Flow<GuidesViewState> {
        return combine(
            guidesRepository.getGuides(),
            resourcesRepository.getHeroImages(),
            resourcesRepository.getItemImages(),
            resourcesRepository.getAbilityImages(),
            resourcesRepository.getAdditionalImages()
        ) { guidesResult, heroImagesResult, itemImagesResult, abilityImagesResult, additionalImagesResult ->
            when {
                guidesResult is RequestResult.Error ||
                        heroImagesResult is RequestResult.Error ||
                        itemImagesResult is RequestResult.Error ||
                        abilityImagesResult is RequestResult.Error ||
                        additionalImagesResult is RequestResult.Error -> {
                    GuidesViewState.Error
                }

                guidesResult is RequestResult.InProgress ||
                        heroImagesResult is RequestResult.InProgress ||
                        itemImagesResult is RequestResult.InProgress ||
                        abilityImagesResult is RequestResult.InProgress ||
                        additionalImagesResult is RequestResult.InProgress -> {
                    GuidesViewState.Loading
                }

                else -> {
                    val guides = (guidesResult as RequestResult.Success).data
                    val heroImages = (heroImagesResult as RequestResult.Success).data
                    val itemImages = (itemImagesResult as RequestResult.Success).data
                    val abilityImages = (abilityImagesResult as RequestResult.Success).data
                    val additionalImages = (additionalImagesResult as RequestResult.Success).data

                    val imageResources =
                        ImageResources(
                            heroImages = heroImages.mapKeys { it.key.id },
                            itemImages = itemImages.mapKeys { it.key.id },
                            abilityImages = abilityImages.mapKeys { it.key.id },
                            additionalImages = additionalImages
                        )
                    GuidesViewState.Display(guides = guides, imageResources = imageResources)
                }
            }
        }.catch {
            emit(GuidesViewState.Error)
        }
    }
}