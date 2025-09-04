package com.alpha.myeyecare.domain

import com.alpha.myeyecare.common.constants.ReminderTypes
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import com.alpha.myeyecare.domain.repository.ReminderRepository
import com.alpha.myeyecare.domain.useCases.CheckReminderStatusUseCase
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class CheckReminderStatusUseCaseTest {

    private lateinit var checkReminderStatusUseCase: CheckReminderStatusUseCase

    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        repository = mockk<ReminderRepository>()
        checkReminderStatusUseCase = CheckReminderStatusUseCase(repository)
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
        coEvery { repository.isReminderEnable(reminderDetails) } returns flowOf(false)

        checkReminderStatusUseCase.invoke(reminderDetails)
    }
}
