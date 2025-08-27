package com.alpha.myeyecare.domain.useCases

import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.repository.ReminderRepository
import javax.inject.Inject

class SaveReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend fun invoke(reminderDetails: ReminderDetails) {
        return repository.insertReminderIntoLocal(reminderDetails)
    }
}
