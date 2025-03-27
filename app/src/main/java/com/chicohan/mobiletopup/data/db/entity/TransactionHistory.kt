package com.chicohan.mobiletopup.data.db.entity


import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.Locale
import java.util.UUID

@Parcelize
@Entity(tableName = "transaction_history")
data class TransactionHistory(
    @PrimaryKey val transactionId: String = UUID.randomUUID().toString(),
    val phoneNumber: String?,
    val providerType: ProviderType,
    val transactionType: TransactionType,
    val amount: Double,
    val currency: String = "MMK",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val transactionTime: Long = System.currentTimeMillis()
) : Parcelable {
    fun getFormattedAmount(): String {
        return "%,.0f %s".format(amount, currency)
    }
    fun getFormattedTransactionTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(transactionTime))
    }
}


enum class TransactionStatus {
    SUCCESS, PENDING, FAILED
}

enum class TransactionType {
    RECHARGE, DATA_PACK
}

fun TransactionType.getDetailTransactionTypeName(providerType: ProviderType): String {
    return when (this) {
        TransactionType.RECHARGE -> "Recharge for ${providerType.getName()}"
        TransactionType.DATA_PACK -> "Buy Data Pack for ${providerType.getName()}"
    }
}
