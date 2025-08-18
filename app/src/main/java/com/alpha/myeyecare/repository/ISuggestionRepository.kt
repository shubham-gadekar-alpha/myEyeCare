package com.alpha.myeyecare.repository

import com.alpha.myeyecare.model.Suggestion

interface ISuggestionRepository {
    suspend fun addSuggestion(suggestion: Suggestion): Result<String>
}