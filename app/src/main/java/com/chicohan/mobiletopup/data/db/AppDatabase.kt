package com.chicohan.mobiletopup.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chicohan.mobiletopup.data.db.dao.TelecomProviderDao
import com.chicohan.mobiletopup.data.db.entity.ListStringConverter
import com.chicohan.mobiletopup.data.db.entity.ProviderTypeConverter
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider


@Database(
    entities = [
        TelecomProvider::class
    ],
    version = 1
)

@TypeConverters(ProviderTypeConverter::class, ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun telecomProviderDao(): TelecomProviderDao
}