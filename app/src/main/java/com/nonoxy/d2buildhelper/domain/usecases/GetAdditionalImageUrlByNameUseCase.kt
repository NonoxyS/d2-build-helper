package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.repository.ResourceClient

class GetAdditionalImageUrlByNameUseCase(private val firebaseClient: ResourceClient) {

    suspend fun execute(utilName: String): String {
        return firebaseClient.getUtilImageUrlByName(utilName)
    }
}