package com.chicohan.mobiletopup.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chicohan.mobiletopup.data.db.dao.TelecomProviderDao
import com.chicohan.mobiletopup.data.db.dao.TransactionHistoryDao
import com.chicohan.mobiletopup.data.db.entity.ListStringConverter
import com.chicohan.mobiletopup.data.db.entity.TopUpDataTypeConverter
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory


@Database(
    entities = [
        TelecomProvider::class,
        TransactionHistory::class
    ],
    version = 2
)

@TypeConverters(TopUpDataTypeConverter::class, ListStringConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun telecomProviderDao(): TelecomProviderDao
    abstract fun transactionHistoryDao(): TransactionHistoryDao
}