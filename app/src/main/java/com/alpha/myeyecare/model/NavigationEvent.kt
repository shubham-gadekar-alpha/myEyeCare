package com.alpha.myeyecare.model

sealed class NavigationEvent {
    data object NavigateToSplash : NavigationEvent()
    data object NavigateToHome : NavigationEvent()
    data object NavigateToEyeCareReminder : NavigationEvent()
    data object NavigateToDrinkWaterReminder : NavigationEvent()
    data object NavigateToSuggestions : NavigationEvent()
    data object NavigateBack : NavigationEvent()
    // Add other navigation events as needed
}
