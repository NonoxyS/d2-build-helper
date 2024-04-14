package com.nonoxy.d2buildhelper.data.repository

import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.nonoxy.d2buildhelper.domain.repository.ResourceClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class FirebaseResourceClient(private val firebaseClient: Firebase): ResourceClient {

    private var firestoreHeroesListener: ListenerRegistration? = null
    override suspend fun getItemNameById(itemIds: List<Int>): MutableMap<Short, String> {
        val itemNames = mutableMapOf<Short, String>()
        
        var firestoreResult = firebaseClient.firestore.collection("items")
            .whereIn("id", itemIds)
            .get(Source.CACHE)
            .await()

        if (firestoreResult.documents.size != itemIds.size) {
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

    override suspend fun getHeroDetailsById(heroIds: List<Int>):
            MutableMap<Short, MutableMap<String, String>> {

        val heroDetails = mutableMapOf<Short, MutableMap<String, String>>()
        var firestoreResult = firebaseClient.firestore.collection("heroes")
            .whereIn("id", heroIds)
            .get(Source.CACHE)
            .await()

        if (firestoreResult.documents.size != heroIds.size) {
            firestoreResult = firebaseClient.firestore.collection("heroes")
                .whereIn("id", heroIds)
                .get(Source.SERVER)
                .await()
        }

        for (document in firestoreResult) {
            heroDetails[document.get("id").toString().toShort()] =
                mutableMapOf("displayName" to document.get("displayName").toString(),
                    "shortName" to document.get("shortName").toString())
        }
        return heroDetails
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

    override suspend fun getEachHeroDetails(): MutableMap<Short, MutableMap<String, String>> {
        return suspendCancellableCoroutine { continuation ->
            val eachHeroDetails = mutableMapOf<Short, MutableMap<String, String>>()
            firestoreHeroesListener = firebaseClient.firestore.collection("heroes")
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        for (document in snapshot) {
                            eachHeroDetails[document.get("id").toString().toShort()] =
                                mutableMapOf(
                                    "displayName" to document.get("displayName").toString(),
                                    "shortName" to document.get("shortName").toString()
                                )
                        }
                        firestoreHeroesListener?.remove()
                        continuation.resume(eachHeroDetails)
                    }
                }
        }
    }
}