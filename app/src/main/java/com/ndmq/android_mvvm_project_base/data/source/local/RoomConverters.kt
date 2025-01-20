package com.ndmq.android_mvvm_project_base.data.source.local

import androidx.room.TypeConverter
import java.util.Date

class RoomConverters {
    
    @TypeConverter
    fun timestampToDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}