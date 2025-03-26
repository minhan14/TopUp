package com.chicohan.mobiletopup.domain.repository

import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import kotlinx.coroutines.flow.Flow

interface TelecomRepository {

    suspend fun detectProviderFromNumber(mobileNumber: String): TelecomProvider?

    fun getTelecomProviders(): Flow<List<TelecomProvider>>

    suspend fun initializeProviders()
} 