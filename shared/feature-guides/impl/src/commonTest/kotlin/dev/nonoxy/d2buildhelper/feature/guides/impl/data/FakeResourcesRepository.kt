package dev.nonoxy.d2buildhelper.feature.guides.impl.data

import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.domain.Ability
import dev.nonoxy.d2buildhelper.core.domain.Hero
import dev.nonoxy.d2buildhelper.core.domain.Item

internal class FakeResourcesRepository(
    var heroImages: Result<Map<Hero, String>> = Result.success(emptyMap()),
    var itemImages: Result<Map<Item, String>> = Result.success(emptyMap()),
    var abilityImages: Result<Map<Ability, String>> = Result.success(emptyMap()),
    var additionalImages: Result<Map<String, String>> = Result.success(emptyMap()),
) : ResourcesRepository {
    override suspend fun getHeroImages(): Result<Map<Hero, String>> = heroImages

    override suspend fun getItemImages(): Result<Map<Item, String>> = itemImages

    override suspend fun getAbilityImages(): Result<Map<Ability, String>> = abilityImages

    override suspend fun getAdditionalImages(): Result<Map<String, String>> = additionalImages
}
