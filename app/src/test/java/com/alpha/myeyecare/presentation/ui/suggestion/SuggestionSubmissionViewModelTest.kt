package com.alpha.myeyecare.presentation.ui.suggestion

import com.alpha.myeyecare.MainDispatcherRule
import com.alpha.myeyecare.domain.model.Suggestion
import com.alpha.myeyecare.domain.useCases.SaveSuggestionsUseCase
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SuggestionSubmissionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var suggestionSubmissionViewModel: SuggestionSubmissionViewModel

    private lateinit var saveSuggestionsUseCase: SaveSuggestionsUseCase

    @Before
    fun setup() {
        saveSuggestionsUseCase = mockk<SaveSuggestionsUseCase>()
        suggestionSubmissionViewModel = SuggestionSubmissionViewModel(saveSuggestionsUseCase)
    }

    @Test
    fun submitSuggestionTest() = runTest {
        val suggestion = Suggestion(
            id = null,
            name = "name",
            email = "email",
            text = "Suggestion Text",
            timestamp = null,
            status = "new"
        )

        coJustRun { saveSuggestionsUseCase.invoke(suggestion) }

        suggestionSubmissionViewModel.submitSuggestion("name", "email", "Suggestion Text")
    }
}
