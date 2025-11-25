package com.example.resqtalk.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.resqtalk.data.dao.AlertHistoryDao
import com.example.resqtalk.data.dao.EmergencyContactDao
import com.example.resqtalk.data.entity.AlertHistory
import com.example.resqtalk.data.entity.EmergencyContact

@Database(entities = [EmergencyContact::class, AlertHistory::class], version = 2, exportSchema = false)
abstract class ResQtalkDatabase : RoomDatabase() {
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun alertHistoryDao(): AlertHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: ResQtalkDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS alert_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        message TEXT NOT NULL,
                        recipientCount INTEGER NOT NULL,
                        successCount INTEGER NOT NULL,
                        failCount INTEGER NOT NULL,
                        location TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): ResQtalkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResQtalkDatabase::class.java,
                    "resqtalk_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
