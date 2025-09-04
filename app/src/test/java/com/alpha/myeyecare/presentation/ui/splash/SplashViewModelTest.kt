package com.alpha.myeyecare.presentation.ui.splash

import app.cash.turbine.test
import com.alpha.myeyecare.MainDispatcherRule
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.test.runTest

class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    lateinit var splashViewModel: SplashViewModel

    @Test
    fun `navigationToHome is true after delay`() = runTest {
        splashViewModel = SplashViewModel()
        splashViewModel.navigationToHome.test {
            assert(awaitItem() == false)
            assert(awaitItem() == true)
        }
    }
}
