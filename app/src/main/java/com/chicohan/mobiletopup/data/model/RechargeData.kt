package com.chicohan.mobiletopup.data.model

import com.chicohan.mobiletopup.data.db.entity.ProviderType

data class RechargeData(
    val id: Int,
    val amount: Double,
    val currency: String = "MMK",
    val providerType: ProviderType
) {
    fun getFormattedAmount(): String {
        return "%,.0f %s".format(amount, currency)
    }
}
