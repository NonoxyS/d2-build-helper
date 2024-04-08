package com.nonoxy.d2buildhelper.domain.usecases

import com.nonoxy.d2buildhelper.domain.repository.ResourceClient

class GetItemNameByIdUseCase(private val firebaseClient: ResourceClient) {

    suspend fun execute(itemIds: List<Int>): MutableMap<Short, String> {
        return firebaseClient.getItemNameById(itemIds)
    }
}