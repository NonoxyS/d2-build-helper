package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.repository.ResourceClient

class GetHeroImageUrlByNameUseCase(private val firebaseClient: ResourceClient) {

    suspend fun execute(shortName: String): String {
        return firebaseClient.getHeroImageUrlByName(shortName)
    }
}