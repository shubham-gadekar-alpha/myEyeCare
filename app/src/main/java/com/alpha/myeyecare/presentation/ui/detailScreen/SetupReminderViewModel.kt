package com.alpha.myeyecare.presentation.ui.detailScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alpha.myeyecare.domain.model.ReminderDetails
import com.alpha.myeyecare.domain.useCases.GetReminderDetailsUserCase
import com.alpha.myeyecare.domain.useCases.SaveReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SetupReminderViewModel @Inject constructor(
    private val getReminderDetailsUserCase: GetReminderDetailsUserCase,
    private val saveReminderUseCase: SaveReminderUseCase,
) : ViewModel() {

    private var _reminderDetails = MutableStateFlow(ReminderDetails())
    val reminderDetails: StateFlow<ReminderDetails> = _reminderDetails

    fun fetchData(reminderType: String) {
        viewModelScope.launch {
            delay(1000)
            getReminderDetailsUserCase.invoke(reminderType).collect {
                _reminderDetails.value = it
            }
        }
    }

    fun updateReminderEnabledStatus(reminderType: String, enableStatus: Boolean) {
        viewModelScope.launch {
            getReminderDetailsUserCase.invoke(reminderType).collect { reminderDetail ->
                saveReminderUseCase.invoke(
                    reminderDetail.apply {
                        type = reminderType
                        isEnabled = enableStatus
                    }
                )
            }
        }
    }

    fun saveReminder(reminderType: String, reminderDetails: ReminderDetails) {
        viewModelScope.launch {
            saveReminderUseCase.invoke(
                reminderDetails.apply {
                    type = reminderType
                }
            )
        }
    }
}
