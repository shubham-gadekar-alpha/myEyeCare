package com.alpha.myeyecare.di

import com.alpha.myeyecare.repository.ISuggestionRepository
import com.alpha.myeyecare.repository.LocalDbRepository
import com.alpha.myeyecare.repository.LocalDbRepositoryImpl
import com.alpha.myeyecare.repository.SuggestionRepository
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
        suggestionRepositoryImpl: SuggestionRepository
    ): ISuggestionRepository

    @Singleton
    @Binds
    abstract fun bindLocalDbRepository(
        localDbRepositoryImpl: LocalDbRepositoryImpl
    ): LocalDbRepository
}
