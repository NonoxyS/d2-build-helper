package dev.nonoxy.d2buildhelper.data

import dev.nonoxy.d2buildhelper.core.data.api.guides.GuidesApi
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.DetailGuideDto
import dev.nonoxy.d2buildhelper.core.data.api.guides.models.GuideDto

internal class FakeGuidesApi(
    var guides: Result<List<GuideDto>> = Result.success(emptyList()),
    var heroGuides: Result<List<GuideDto>> = Result.success(emptyList()),
) : GuidesApi {
    override suspend fun getGuides(): Result<List<GuideDto>> = guides

    override suspend fun getHeroGuides(heroId: Short): Result<List<GuideDto>> = heroGuides

    override suspend fun getDetailGuide(matchId: Long, steamAccountId: Long): Result<DetailGuideDto> =
        Result.failure(NotImplementedError("Not used in tests"))
}
