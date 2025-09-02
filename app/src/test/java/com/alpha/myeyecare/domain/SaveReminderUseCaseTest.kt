package com.alpha.myeyecare.domain

import com.alpha.myeyecare.common.constants.ReminderTypes
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.model.ReminderFrequency
import com.alpha.myeyecare.domain.repository.ReminderRepository
import com.alpha.myeyecare.domain.useCases.SaveReminderUseCase
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveReminderUseCaseTest {

    private lateinit var saveReminderUseCase: SaveReminderUseCase
    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        repository = mockk()
        saveReminderUseCase = SaveReminderUseCase(repository)
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

        coJustRun { repository.insertReminderIntoLocal(reminderDetails) }
        saveReminderUseCase.invoke(reminderDetails)
    }
}
