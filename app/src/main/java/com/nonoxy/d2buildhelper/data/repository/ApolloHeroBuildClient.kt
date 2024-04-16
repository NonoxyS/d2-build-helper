package com.nonoxy.d2buildhelper.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.nonoxy.GuidesInfoQuery
import com.nonoxy.HeroBuildQuery
import com.nonoxy.HeroGuidesInfoQuery
import com.nonoxy.d2buildhelper.data.toGuideInfo
import com.nonoxy.d2buildhelper.data.toHeroGuideBuild
import com.nonoxy.d2buildhelper.data.toHeroGuideInfo
import com.nonoxy.d2buildhelper.domain.repository.HeroBuildClient
import com.nonoxy.d2buildhelper.domain.model.HeroGuideBuild
import com.nonoxy.d2buildhelper.domain.model.HeroGuideInfo

class ApolloHeroBuildClient(private val apolloClient: ApolloClient) : HeroBuildClient {
    override suspend fun getGuidesInfo(): List<HeroGuideInfo> {
        return apolloClient.query(
            GuidesInfoQuery())
            .execute()
            .data
            ?.heroStats?.guide
            ?.mapNotNull { it?.toGuideInfo() }
            ?: emptyList()
    }

    // Always cast Short to Int, because there is no JsonWriter adapter to serialize Short value
    // Need to add CustomScalarAdapter to ApolloClient
    override suspend fun getHeroGuidesInfo(heroId: Short): List<HeroGuideInfo> {
        return apolloClient.query(
            HeroGuidesInfoQuery(Optional.presentIfNotNull(heroId.toInt())))
            .execute()
            .data
            ?.heroStats?.guide
            ?.mapNotNull { it?.toHeroGuideInfo() }
            ?: emptyList()
    }

    override suspend fun getHeroGuideBuild(matchId: Long, steamAccountId: Long): HeroGuideBuild {
        return apolloClient.query(
            HeroBuildQuery(matchId as Any, Optional.presentIfNotNull(steamAccountId as Any?))
        )
            .execute()
            .data
            ?.match.let {
                match -> match?.players?.firstNotNullOfOrNull{ player ->
                    player?.toHeroGuideBuild(match.durationSeconds?: 0) }!! }

    }
}