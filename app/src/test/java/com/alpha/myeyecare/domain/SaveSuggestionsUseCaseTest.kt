package com.alpha.myeyecare.domain

import com.alpha.myeyecare.data.repository.SuggestionRepositoryImpl
import com.alpha.myeyecare.domain.model.Suggestion
import com.alpha.myeyecare.domain.useCases.SaveSuggestionsUseCase
import io.mockk.coJustRun
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveSuggestionsUseCaseTest {

    private lateinit var saveSuggestionsUseCase: SaveSuggestionsUseCase

    private lateinit var repository: SuggestionRepositoryImpl

    @Before
    fun setup() {
        repository = mockk()
        saveSuggestionsUseCase = SaveSuggestionsUseCase(repository)
    }

    @Test
    fun invokeTest() = runTest {
        val suggestion = Suggestion(
            id = null,
            name = "name",
            email = "email",
            text = "Suggestion Text",
            timestamp = null,
            status = "new"
        )
        coJustRun { repository.addSuggestion(suggestion) }
        saveSuggestionsUseCase.invoke(suggestion)
    }
}
