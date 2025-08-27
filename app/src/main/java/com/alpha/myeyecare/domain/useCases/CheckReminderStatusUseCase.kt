package com.alpha.myeyecare.domain.useCases

import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckReminderStatusUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    fun invoke(reminder: ReminderDetails): Flow<Boolean> {
        return repository.isReminderEnable(reminder)
    }
}
