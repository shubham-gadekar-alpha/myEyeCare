package com.alpha.myeyecare.data.repository

import com.alpha.myeyecare.domain.model.Suggestion
import com.alpha.myeyecare.domain.repository.SuggestionRepository
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class SuggestionRepositoryImpl @Inject constructor(
    private val firebaseDb: FirebaseFirestore
) : SuggestionRepository {

    override suspend fun addSuggestion(suggestion: Suggestion): Result<String> {
        val documentReference = firebaseDb.collection("suggestions")
            .add(suggestion)
            .await()
        return Result.success(documentReference.id)
    }
}
