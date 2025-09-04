package com.alpha.myeyecare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alpha.myeyecare.data.local.entities.Reminder

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminder WHERE id = :id LIMIT 1")
    suspend fun getReminderDetails(id: String): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminderDetails(reminder: Reminder)

    @Query("SELECT isEnabled FROM reminder WHERE id = :id")
    suspend fun isReminderEnable(id: String): Boolean
}
