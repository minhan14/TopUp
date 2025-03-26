package com.chicohan.mobiletopup.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "telecom_providers")
data class TelecomProvider(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: ProviderType,
    val logoPath: Int,
    val prefixes: List<String>
)

enum class ProviderType {
    ATOM, MPT, OOREDOO, UNKNOWN
}

fun ProviderType.getName(): String {
    return when (this) {
        ProviderType.ATOM -> "Atom Myanmar"
        ProviderType.MPT -> "Mpt Myanmar"
        ProviderType.OOREDOO -> "Ooredoo Myanmar"
        ProviderType.UNKNOWN -> "Unknown Operator"
    }
}