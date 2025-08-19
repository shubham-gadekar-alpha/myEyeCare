package com.alpha.myeyecare.repository

import android.util.Log
import com.alpha.myeyecare.model.Suggestion
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestionRepository @Inject constructor() : ISuggestionRepository {
    private val db = Firebase.firestore

    override suspend fun addSuggestion(suggestion: Suggestion): Result<String> {
        return try {
            val documentReference = db.collection("suggestions")
                .add(suggestion)
                .await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Error adding suggestion", e)
            Result.failure(e)
        }
    }
}