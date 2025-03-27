package com.chicohan.mobiletopup.data.db.entity

import androidx.room.TypeConverter

class TopUpDataTypeConverter {
    @TypeConverter
    fun fromProviderType(value: ProviderType): String {
        return value.name
    }

    @TypeConverter
    fun toProviderType(value: String): ProviderType {
        return try {
            ProviderType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ProviderType.UNKNOWN
        }
    }

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String {
        return type.name
    }

    @TypeConverter
    fun toTransactionType(type: String): TransactionType {
        return TransactionType.valueOf(type)
    }

    @TypeConverter
    fun fromTransactionStatus(status: TransactionStatus): String {
        return status.name
    }

    @TypeConverter
    fun toTransactionStatus(status: String): TransactionStatus {
        return TransactionStatus.valueOf(status)
    }
}