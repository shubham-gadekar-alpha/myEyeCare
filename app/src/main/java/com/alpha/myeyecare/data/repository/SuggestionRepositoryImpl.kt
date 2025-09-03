package com.alpha.myeyecare.data.repository

import android.util.Log
import com.alpha.myeyecare.domain.model.Suggestion
import com.alpha.myeyecare.domain.repository.SuggestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuggestionRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseFirestore
) : SuggestionRepository {

    override suspend fun addSuggestion(suggestion: Suggestion): Result<String> {
        return try {
            val documentReference = firebaseDb.collection("suggestions")
                .add(suggestion)
                .await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.w("FirestoreRepo", "Error adding suggestion", e)
            Result.failure(e)
        }
    }
}
