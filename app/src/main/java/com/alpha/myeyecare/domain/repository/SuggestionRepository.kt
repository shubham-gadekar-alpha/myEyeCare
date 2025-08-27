package com.alpha.myeyecare.domain.repository

import com.alpha.myeyecare.domain.model.Suggestion

interface SuggestionRepository {
    suspend fun addSuggestion(suggestion: Suggestion): Result<String>
}
