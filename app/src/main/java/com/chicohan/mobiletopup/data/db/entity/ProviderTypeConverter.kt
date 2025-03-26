package com.chicohan.mobiletopup.data.db.entity

import androidx.room.TypeConverter

class ProviderTypeConverter {
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
}