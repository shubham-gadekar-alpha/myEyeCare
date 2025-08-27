package com.alpha.myeyecare.di

import android.content.Context
import androidx.room.Room
import com.alpha.myeyecare.data.local.ReminderDatabase
import com.alpha.myeyecare.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): ReminderDatabase {
        return Room.databaseBuilder(context, ReminderDatabase::class.java, "reminder_db").build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(reminderDatabase: ReminderDatabase): ReminderDao {
        return reminderDatabase.getReminderDao()
    }
}
