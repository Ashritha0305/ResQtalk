package com.example.resqtalk.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.resqtalk.data.entity.AlertHistory

@Dao
interface AlertHistoryDao {
    @Insert
    suspend fun insertAlert(alert: AlertHistory)

    @Query("SELECT * FROM alert_history ORDER BY timestamp DESC")
    suspend fun getAllAlerts(): List<AlertHistory>

    @Query("SELECT COUNT(*) FROM alert_history")
    suspend fun getTotalAlerts(): Int

    @Query("SELECT SUM(successCount) FROM alert_history")
    suspend fun getTotalSuccessful(): Int?

    @Query("SELECT SUM(failCount) FROM alert_history")
    suspend fun getTotalFailed(): Int?

    @Query("DELETE FROM alert_history")
    suspend fun deleteAll()
}
