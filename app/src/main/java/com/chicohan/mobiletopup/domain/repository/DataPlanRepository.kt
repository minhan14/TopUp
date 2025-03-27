package com.chicohan.mobiletopup.domain.repository

import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.model.DataPlan
import com.chicohan.mobiletopup.data.model.RechargeData
import kotlinx.coroutines.flow.Flow

interface DataPlanRepository {
    fun getDataPlansByProvider(provider: TelecomProvider): Flow<List<DataPlan>>
    fun getRechargePlanByProvider(provider: TelecomProvider): Flow<List<RechargeData>>
}