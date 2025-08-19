package com.alpha.myeyecare.model

// --- UI States for different screens/features ---
sealed class SuggestionSubmissionUiState {
    object Idle : SuggestionSubmissionUiState()
    object Loading : SuggestionSubmissionUiState()
    data class Success(val message: String) : SuggestionSubmissionUiState()
    data class Error(val message: String) : SuggestionSubmissionUiState()
}