package com.chicohan.mobiletopup.domain.repository

import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.model.DataPlan
import kotlinx.coroutines.flow.Flow

interface DataPlanRepository {
    fun getDataPlansByProvider(provider: TelecomProvider): Flow<List<DataPlan>>
}