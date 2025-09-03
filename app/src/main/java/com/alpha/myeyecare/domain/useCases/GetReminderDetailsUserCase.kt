package com.alpha.myeyecare.domain.useCases

import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.repository.ReminderRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetReminderDetailsUserCase @Inject constructor(
    private val repository: ReminderRepository
) {
    fun invoke(id: String): Flow<ReminderDetails> {
        return repository.getReminderDetails(id)
    }
}
