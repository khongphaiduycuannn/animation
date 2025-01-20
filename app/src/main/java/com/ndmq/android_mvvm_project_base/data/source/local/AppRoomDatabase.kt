package com.ndmq.android_mvvm_project_base.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ndmq.android_mvvm_project_base.data.model.DrawProject

@Database(
    entities = [DrawProject::class],
    version = 1, exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract val projectDao: ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppRoomDatabase? = null

        fun getDatabase(context: Context): AppRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppRoomDatabase::class.java,
                    "room_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}