package dev.nonoxy.d2buildhelper.feature.guides.impl.data

import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.GuidesApi
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.feature.guides.impl.data.api.models.GuideDto

internal class FakeGuidesApi(
    var guides: Result<List<GuideDto>> = Result.success(emptyList()),
    var heroGuides: Result<List<GuideDto>> = Result.success(emptyList()),
) : GuidesApi {
    override suspend fun getGuides(): Result<List<GuideDto>> = guides

    override suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>> = heroGuides

    override suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto> =
        Result.failure(NotImplementedError("Not used in tests"))
}
