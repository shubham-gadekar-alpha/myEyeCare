package com.alpha.myeyecare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alpha.myeyecare.data.local.converters.Converters
import com.alpha.myeyecare.data.local.dao.ReminderDao
import com.alpha.myeyecare.data.local.entities.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun getReminderDao(): ReminderDao
}
