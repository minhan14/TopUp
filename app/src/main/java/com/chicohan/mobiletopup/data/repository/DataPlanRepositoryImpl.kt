package com.chicohan.mobiletopup.data.repository

import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.model.DataPlan

import com.chicohan.mobiletopup.domain.repository.DataPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DataPlanRepositoryImpl @Inject constructor() : DataPlanRepository {
    private val dataPlans = listOf(
        DataPlan(
            id = "ooredoo_1",
            providerType = ProviderType.OOREDOO,
            description = "10GB for 30 days",
            duration = "Valid for 30 days",
            amount = 5000.0,
            dataAmount = "10GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_leave

        ),
        DataPlan(
            id = "ooredoo_2",
            providerType = ProviderType.OOREDOO,
            description = "20GB for 30 days",
            duration = "Valid for 30 days",
            amount = 8000.0,
            dataAmount = "20GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_cat

        ),
        DataPlan(
            id = "ooredoo_3",
            providerType = ProviderType.OOREDOO,
            description = "5GB for 7 days",
            duration = "Valid for 7 days",
            amount = 2000.0,
            dataAmount = "5GB",
            validityDays = 7,
            iconResId = R.drawable.icon_leave

        ),

        DataPlan(
            id = "atom_1",
            providerType = ProviderType.ATOM,
            description = "15GB for 30 days",
            duration = "Valid for 30 days",
            amount = 6000.0,
            dataAmount = "15GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_cat

        ),
        DataPlan(
            id = "atom_2",
            providerType = ProviderType.ATOM,
            description = "25GB for 30 days",
            duration = "Valid for 30 days",
            amount = 9000.0,
            dataAmount = "25GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_leave

        ),
        DataPlan(
            id = "atom_3",
            providerType = ProviderType.ATOM,
            description = "8GB for 7 days",
            duration = "Valid for 7 days",
            amount = 2500.0,
            dataAmount = "8GB",
            validityDays = 7,
            iconResId = R.drawable.icon_cat

        ),

        DataPlan(
            id = "mpt_1",
            providerType = ProviderType.MPT,
            description = "12GB for 30 days",
            duration = "Valid for 30 days",
            amount = 5500.0,
            dataAmount = "12GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_cat

        ),
        DataPlan(
            id = "mpt_2",
            providerType = ProviderType.MPT,
            description = "22GB for 30 days",
            duration = "Valid for 30 days",
            amount = 8500.0,
            dataAmount = "22GB",
            validityDays = 30,
            isPopular = true,
            iconResId = R.drawable.icon_leave

        ),
        DataPlan(
            id = "mpt_3",
            providerType = ProviderType.MPT,
            description = "6GB for 7 days",
            duration = "Valid for 7 days",
            amount = 2200.0,
            dataAmount = "6GB",
            validityDays = 7,
            iconResId = R.drawable.icon_cat
        )
    )


    override fun getDataPlansByProvider(provider: TelecomProvider): Flow<List<DataPlan>> = flow {
        emit(dataPlans.filter { it.providerType == provider.type })
    }


}