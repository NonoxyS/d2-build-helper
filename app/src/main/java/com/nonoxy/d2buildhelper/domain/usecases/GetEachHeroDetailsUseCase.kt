package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.repository.ResourceClient

class GetEachHeroDetailsUseCase(private val firebaseClient: ResourceClient) {

    suspend fun execute(): MutableMap<Short, MutableMap<String, String>> {
        return firebaseClient.getEachHeroDetails()
    }
}