package com.alpha.myeyecare.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private var _navigationToHome = MutableStateFlow(false)
    val navigationToHome: StateFlow<Boolean> = _navigationToHome

    init {
        viewModelScope.launch {
            delay(2000)
            _navigationToHome.value = true
        }
    }
}
