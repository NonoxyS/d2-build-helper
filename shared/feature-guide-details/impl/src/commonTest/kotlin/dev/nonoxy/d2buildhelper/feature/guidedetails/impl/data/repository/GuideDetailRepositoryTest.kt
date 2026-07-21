package dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.repository

import dev.nonoxy.d2buildhelper.core.domain.models.HeroId
import dev.nonoxy.d2buildhelper.core.match.domain.MatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.FakeGuideDetailApiClient
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.TestCoroutineDispatchers
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.guideDetailResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.mappers.GuideDetailMapperImpl
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteGuideDetailPlayerResponse
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.data.network.models.RemoteMatchPlayerPosition
import dev.nonoxy.d2buildhelper.feature.guidedetails.impl.domain.repository.GuideDetailRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GuideDetailRepositoryTest {

    private fun repository(api: FakeGuideDetailApiClient): GuideDetailRepository =
        GuideDetailRepositoryImpl(api, GuideDetailMapperImpl(), TestCoroutineDispatchers())

    @Test
    fun `getGuideDetail maps DTO to domain`() = runTest {
        val response = guideDetailResponse(
            matchId = 7891234567L,
            steamAccountId = 123456789L,
            player = RemoteGuideDetailPlayerResponse(
                heroId = 8,
                isRadiant = true,
                position = RemoteMatchPlayerPosition.POSITION_2,
            ),
        )
        val api = FakeGuideDetailApiClient(detail = Result.success(response))

        val detail = repository(api).getGuideDetail(matchId = 7891234567L, steamAccountId = 123456789L).getOrThrow()

        assertEquals(7891234567L, detail.matchId)
        assertEquals(123456789L, detail.steamAccountId)
        assertEquals(HeroId(8), detail.player.heroId)
        assertEquals(MatchPlayerPosition.POSITION_2, detail.player.position)
    }

    @Test
    fun `getGuideDetail forwards matchId and steamAccountId to the api client`() = runTest {
        val api = FakeGuideDetailApiClient()

        repository(api).getGuideDetail(matchId = 42L, steamAccountId = 99L).getOrThrow()

        assertEquals(42L, api.lastMatchId)
        assertEquals(99L, api.lastSteamAccountId)
    }

    @Test
    fun `getGuideDetail surfaces upstream failure unchanged`() = runTest {
        val boom = IllegalStateException("network")
        val api = FakeGuideDetailApiClient(detail = Result.failure(boom))

        val result = repository(api).getGuideDetail(matchId = 1L, steamAccountId = 2L)

        assertTrue(result.isFailure)
        assertEquals(boom, result.exceptionOrNull())
    }
}
