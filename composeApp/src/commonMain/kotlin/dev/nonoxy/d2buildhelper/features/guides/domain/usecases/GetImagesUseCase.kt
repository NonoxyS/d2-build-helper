package dev.nonoxy.d2buildhelper.features.guides.domain.usecases

import dev.nonoxy.d2buildhelper.core.data.RequestResult
import dev.nonoxy.d2buildhelper.core.data.api.resources.image.models.ImageResources
import dev.nonoxy.d2buildhelper.core.data.repository.resources.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.di.InjectProvider
import dev.nonoxy.d2buildhelper.features.guides.domain.models.HeroUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn

internal class GetImagesUseCase(
    private val resourcesRepository: ResourcesRepository = InjectProvider.getDependency(
        ResourcesRepository::class
    )
) {
    internal operator fun invoke(
        isNeedHeroImages: Boolean = true,
        isNeedItemImages: Boolean = true,
        isNeedAbilityImages: Boolean = true,
        isNeedAdditionalImages: Boolean = true,
    ): Flow<RequestResult<ImageResources>> {
        return combine(
            if (isNeedHeroImages) resourcesRepository.getHeroImages() else flowOf(
                RequestResult.Success(
                    emptyMap()
                )
            ),
            if (isNeedItemImages) resourcesRepository.getItemImages() else flowOf(
                RequestResult.Success(
                    emptyMap()
                )
            ),
            if (isNeedAbilityImages) resourcesRepository.getAbilityImages() else flowOf(
                RequestResult.Success(
                    emptyMap()
                )
            ),
            if (isNeedAdditionalImages) resourcesRepository.getAdditionalImages() else flowOf(
                RequestResult.Success(
                    emptyMap()
                )
            ),
        ) { heroImagesResult, itemImagesResult, abilityImagesResult, additionalImagesResult ->
            when {
                heroImagesResult is RequestResult.Error ||
                        itemImagesResult is RequestResult.Error ||
                        abilityImagesResult is RequestResult.Error ||
                        additionalImagesResult is RequestResult.Error -> RequestResult.Error()

                heroImagesResult is RequestResult.InProgress ||
                        itemImagesResult is RequestResult.InProgress ||
                        abilityImagesResult is RequestResult.InProgress ||
                        additionalImagesResult is RequestResult.InProgress -> RequestResult.InProgress()

                else -> {
                    val heroImages = (heroImagesResult as RequestResult.Success).data
                    val itemImages = (itemImagesResult as RequestResult.Success).data
                    val abilityImages = (abilityImagesResult as RequestResult.Success).data
                    val additionalImages = (additionalImagesResult as RequestResult.Success).data

                    val imageResources =
                        ImageResources(
                            heroImages = heroImages.mapKeys {
                                HeroUI(
                                    heroId = it.key.id,
                                    shortName = it.key.shortName,
                                    displayName = it.key.displayName
                                )
                            },
                            itemImages = itemImages.mapKeys { it.key.id },
                            abilityImages = abilityImages.mapKeys { it.key.id },
                            additionalImages = additionalImages
                        )
                    RequestResult.Success(imageResources)
                }
            }
        }.flowOn(Dispatchers.Default).catch { e -> emit(RequestResult.Error(e)) }
    }
}