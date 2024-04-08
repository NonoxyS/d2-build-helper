package com.nonoxy.d2buildhelper.data.repository

import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nonoxy.d2buildhelper.domain.repository.ResourceClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class FirebaseResourceClient(private val firebaseClient: Firebase): ResourceClient {
    override suspend fun getItemNameById(itemIds: List<Int>): MutableMap<Short, String> {
        val itemNames = mutableMapOf<Short, String>()
        
        var firestoreResult = firebaseClient.firestore.collection("items")
            .whereIn("id", itemIds)
            .get(Source.CACHE)
            .await()

        if (firestoreResult.documents.isEmpty()) {
            firestoreResult = firebaseClient.firestore.collection("items")
                .whereIn("id", itemIds)
                .get(Source.SERVER)
                .await()
        }

        for (document in firestoreResult) {
            itemNames[document.get("id").toString().toShort()] =
                document.get("shortName").toString()
        }

        return itemNames
    }

    override suspend fun getHeroNameById(heroIds: List<Int>):
            MutableMap<Short, MutableMap<String, String>> {
        val heroNames = mutableMapOf<Short, MutableMap<String, String>>()

        var firestoreResult = firebaseClient.firestore.collection("heroes")
            .whereIn("id", heroIds)
            .get(Source.CACHE)
            .await()

        if (firestoreResult.documents.isEmpty()) {
            firestoreResult = firebaseClient.firestore.collection("heroes")
                .whereIn("id", heroIds)
                .get(Source.SERVER)
                .await()
        }

        for (document in firestoreResult) {
            heroNames[document.get("id").toString().toShort()] =
                mutableMapOf("displayName" to document.get("displayName").toString(),
                    "shortName" to document.get("shortName").toString())
        }

        return heroNames
    }

    override suspend fun getHeroImageUrlByName(heroName: String):
            String = suspendCancellableCoroutine { continuation ->
        firebaseClient.storage.reference
            .child("hero_icons/${heroName}_minimap_icon.png").downloadUrl
            .addOnSuccessListener { uri ->
                continuation.resume(uri.toString())
            }
            .addOnFailureListener {
                continuation.resume("null")
            }
    }

    override suspend fun getItemImageUrlByName(itemName: String):
            String = suspendCancellableCoroutine { continuation ->

        firebaseClient.storage.reference
            .child("item_icons/$itemName.png").downloadUrl
            .addOnSuccessListener { uri ->
                continuation.resume(uri.toString())
            }
            .addOnFailureListener {
                continuation.resume("null")
            }
    }

    override suspend fun getUtilImageUrlByName(utilName: String):
            String = suspendCancellableCoroutine { continuation ->

        firebaseClient.storage.reference
            .child("util_icons/$utilName.png").downloadUrl
            .addOnSuccessListener { uri ->
                continuation.resume(uri.toString())
            }
            .addOnFailureListener {
                continuation.resume("null")
            }
    }

}