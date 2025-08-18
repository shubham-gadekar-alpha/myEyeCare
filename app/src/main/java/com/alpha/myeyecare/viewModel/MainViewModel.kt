package com.alpha.myeyecare.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.myeyecare.model.NavigationEvent
import com.alpha.myeyecare.model.Suggestion
import com.alpha.myeyecare.model.SuggestionSubmissionUiState
import com.alpha.myeyecare.repository.ISuggestionRepository
import com.alpha.myeyecare.repository.LocalDbRepository
import com.alpha.myeyecare.ui.screens.ReminderDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val suggestionRepository: ISuggestionRepository,
    private val localDb: LocalDbRepository
) : ViewModel() {

    // --- Navigation State ---
    // Using SharedFlow for one-time navigation events
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    // --- Splash Screen Logic ---
    private val _isSplashScreenDone = MutableStateFlow(false)
    val isSplashScreenDone: StateFlow<Boolean> = _isSplashScreenDone.asStateFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000L) // Simulate splash delay
            _isSplashScreenDone.value = true
            _navigationEvent.emit(NavigationEvent.NavigateToHome) // Navigate to home after splash
        }
    }

    // --- Suggestion Screen Logic ---
    private val _suggestionUiState =
        MutableStateFlow<SuggestionSubmissionUiState>(SuggestionSubmissionUiState.Idle)
    val suggestionUiState: StateFlow<SuggestionSubmissionUiState> = _suggestionUiState.asStateFlow()

    fun submitSuggestion(name: String, email: String, text: String) {
        if (name.isBlank() || text.isBlank()) {
            _suggestionUiState.value =
                SuggestionSubmissionUiState.Error("Name and suggestion are required.")
            return
        }
        viewModelScope.launch {
            _suggestionUiState.value = SuggestionSubmissionUiState.Loading
            val newSuggestion = Suggestion(name = name, email = email, text = text)
            suggestionRepository.addSuggestion(newSuggestion).fold(
                onSuccess = { docId ->
                    _suggestionUiState.value =
                        SuggestionSubmissionUiState.Success("Suggestion submitted! ID: $docId")
                    onNavigateBack()
                },
                onFailure = { exception ->
                    _suggestionUiState.value =
                        SuggestionSubmissionUiState.Error(exception.message ?: "Submission failed.")
                }
            )
        }
    }

    fun updateReminderEnabledStatus(reminderId: String, isEnabled: Boolean): Boolean {
        return localDb.updateReminderEnabledStatus(reminderId = reminderId, isEnabled = isEnabled)
    }

    fun getReminder(reminderId: String): ReminderDetails? {
        return localDb.getReminder(reminderId)
    }

    fun saveReminder(reminderId: String, details: ReminderDetails) {
        return localDb.saveReminder(reminderId, details)
    }

    fun resetSuggestionUiState() {
        _suggestionUiState.value = SuggestionSubmissionUiState.Idle
    }


    // --- Navigation Triggers ---
    fun onGoToSuggestionsClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToSuggestions)
        }
    }

    fun onNavigateBack() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }
}
