package com.chicohan.mobiletopup.data.model

import android.os.Parcelable
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataPlan(
    val id: String,
    val providerType: ProviderType,
    val description: String,
    val duration: String,
    val amount: Double,
    val currency: String = "MMK",
    val dataAmount: String,
    val validityDays: Int,
    val isPopular: Boolean = false,
    val iconResId: Int? = null
) : Parcelable {
    fun getFormattedAmount(): String {
        return "%,.0f %s".format(amount, currency)
    }

    fun getFormattedDescription(): String {
        return "$dataAmount for $validityDays days"
    }

    fun getFormattedDuration(): String {
        return "Valid for $validityDays days"
    }
}
