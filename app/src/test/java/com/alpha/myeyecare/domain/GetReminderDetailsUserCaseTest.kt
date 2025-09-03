package com.alpha.myeyecare.domain

import com.alpha.myeyecare.common.constants.ReminderTypes
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import com.alpha.myeyecare.domain.repository.ReminderRepository
import com.alpha.myeyecare.domain.useCases.GetReminderDetailsUserCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetReminderDetailsUserCaseTest {

    private lateinit var getReminderDetailsUserCase: GetReminderDetailsUserCase
    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        repository = mockk<ReminderRepository>()
        getReminderDetailsUserCase = GetReminderDetailsUserCase(repository)
    }

    @Test
    fun invokeTest() = runTest {
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
        coEvery { repository.getReminderDetails(ReminderTypes.EYE_REMINDER) } returns flowOf(
            reminderDetails
        )

        getReminderDetailsUserCase.invoke(ReminderTypes.EYE_REMINDER)
    }
}
