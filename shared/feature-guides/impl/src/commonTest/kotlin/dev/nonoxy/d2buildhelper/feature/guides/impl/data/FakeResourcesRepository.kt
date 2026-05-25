package dev.nonoxy.d2buildhelper.feature.guides.impl.data

import dev.nonoxy.d2buildhelper.core.resources.data.repository.ResourcesRepository
import dev.nonoxy.d2buildhelper.core.domain.models.Ability
import dev.nonoxy.d2buildhelper.core.domain.models.Hero
import dev.nonoxy.d2buildhelper.core.domain.models.Item

internal class FakeResourcesRepository(
    var heroImages: Result<Map<Hero, String>> = Result.success(emptyMap()),
    var itemImages: Result<Map<Item, String>> = Result.success(emptyMap()),
    var abilityImages: Result<Map<Ability, String>> = Result.success(emptyMap()),
) : ResourcesRepository {
    override suspend fun getHeroImages(): Result<Map<Hero, String>> = heroImages

    override suspend fun getItemImages(): Result<Map<Item, String>> = itemImages

    override suspend fun getAbilityImages(): Result<Map<Ability, String>> = abilityImages
}
