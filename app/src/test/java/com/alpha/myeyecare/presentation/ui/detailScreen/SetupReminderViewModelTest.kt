package com.alpha.myeyecare.presentation.ui.detailScreen

import app.cash.turbine.test
import com.alpha.myeyecare.MainDispatcherRule
import com.alpha.myeyecare.common.constants.ReminderTypes
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import com.alpha.myeyecare.domain.useCases.GetReminderDetailsUserCase
import com.alpha.myeyecare.domain.useCases.SaveReminderUseCase
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SetupReminderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var setupReminderViewModel: SetupReminderViewModel
    private lateinit var getReminderDetailsUserCase: GetReminderDetailsUserCase
    private lateinit var saveReminderUseCase: SaveReminderUseCase

    @Before
    fun setup() {
        getReminderDetailsUserCase = mockk<GetReminderDetailsUserCase>()
        saveReminderUseCase = mockk<SaveReminderUseCase>()

        setupReminderViewModel =
            SetupReminderViewModel(getReminderDetailsUserCase, saveReminderUseCase)
    }

    @Test
    fun fetchDataTest() = runTest {
        val reminderDetails = ReminderDetails(
            type = ReminderTypes.EYE_REMINDER,
            title = "Undefined",
            hour = 10,
            minute = 30,
            frequency = ReminderFrequency.DAILY,
            selectedDays = emptySet(),
            customIntervalMinutes = 60,
            startDateMillis = System.currentTimeMillis(),
            isEnabled = false
        )
        setupReminderViewModel.reminderDetails.test {
            assert(this@test.awaitItem().type == ReminderTypes.UNDEFINED)
        }
        coEvery { getReminderDetailsUserCase.invoke(ReminderTypes.EYE_REMINDER) } returns flowOf(
            reminderDetails
        )

        setupReminderViewModel.fetchData(ReminderTypes.EYE_REMINDER)

        delay(1200)
        setupReminderViewModel.reminderDetails.test {
            assert(this@test.awaitItem().type == reminderDetails.type)
        }
    }

    @Test
    fun updateReminderEnabledStatusTest() = runTest {
        val reminderDetails = ReminderDetails(
            type = ReminderTypes.EYE_REMINDER,
            title = "Undefined",
            hour = 10,
            minute = 30,
            frequency = ReminderFrequency.DAILY,
            selectedDays = emptySet(),
            customIntervalMinutes = 60,
            startDateMillis = System.currentTimeMillis(),
            isEnabled = true
        )
        coEvery { getReminderDetailsUserCase.invoke(ReminderTypes.EYE_REMINDER) } returns flowOf(
            reminderDetails
        )
        coJustRun {
            saveReminderUseCase.invoke(reminderDetails)
        }

        setupReminderViewModel.reminderDetails.test {
            assert(this@test.awaitItem().type == ReminderTypes.UNDEFINED)
        }

        setupReminderViewModel.updateReminderEnabledStatus(ReminderTypes.EYE_REMINDER, true)
    }

    @Test
    fun saveReminderTest() = runTest {
        val reminderDetails = ReminderDetails(
            type = ReminderTypes.EYE_REMINDER,
            title = "Undefined",
            hour = 10,
            minute = 30,
            frequency = ReminderFrequency.DAILY,
            selectedDays = emptySet(),
            customIntervalMinutes = 60,
            startDateMillis = System.currentTimeMillis(),
            isEnabled = true
        )

        coJustRun { saveReminderUseCase.invoke(reminderDetails) }

        setupReminderViewModel.saveReminder(ReminderTypes.EYE_REMINDER, reminderDetails)
    }
}
