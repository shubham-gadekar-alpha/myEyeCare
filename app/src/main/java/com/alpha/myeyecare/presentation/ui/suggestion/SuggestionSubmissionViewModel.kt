package com.alpha.myeyecare.presentation.ui.suggestion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.myeyecare.domain.model.Suggestion
import com.alpha.myeyecare.domain.useCases.SaveSuggestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SuggestionSubmissionViewModel @Inject constructor(
    private val saveSuggestionsUseCase: SaveSuggestionsUseCase
) : ViewModel() {

    fun submitSuggestion(name: String, email: String, suggestionText: String) {
        viewModelScope.launch {
            saveSuggestionsUseCase.invoke(
                Suggestion(
                    name = name,
                    email = email,
                    text = suggestionText
                )
            )
        }
    }
}
