package com.alpha.myeyecare.domain.useCases

import com.alpha.myeyecare.data.repository.SuggestionRepositoryImpl
import com.alpha.myeyecare.domain.model.Suggestion
import javax.inject.Inject

class SaveSuggestionsUseCase @Inject constructor(
    private val repository: SuggestionRepositoryImpl
) {
    suspend fun invoke(suggestion: Suggestion) {
        repository.addSuggestion(suggestion)
    }
}
