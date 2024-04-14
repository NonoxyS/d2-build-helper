package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.repository.ResourceClient

class GetHeroDetailsByIdUseCase(private val firebaseClient: ResourceClient) {

    suspend fun execute(heroIds: List<Int>): MutableMap<Short, MutableMap<String, String>> {
        return firebaseClient.getHeroDetailsById(heroIds)
    }
}