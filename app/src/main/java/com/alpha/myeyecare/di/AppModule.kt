package com.alpha.myeyecare.di

import com.alpha.myeyecare.data.repository.ReminderRepositoryImpl
import com.alpha.myeyecare.domain.repository.SuggestionRepository
import com.alpha.myeyecare.data.repository.SuggestionRepositoryImpl
import com.alpha.myeyecare.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSuggestionRepository(
        suggestionRepositoryImpl: SuggestionRepositoryImpl
    ): SuggestionRepository

    @Singleton
    @Binds
    abstract fun bindLocalDbRepository(
        localDbRepositoryImpl: LocalDbRepositoryImpl
    ): LocalDbRepository

    @Singleton
    @Binds
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository
}
