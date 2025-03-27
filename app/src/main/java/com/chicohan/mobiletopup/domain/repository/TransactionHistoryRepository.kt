package com.chicohan.mobiletopup.domain.repository

import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import kotlinx.coroutines.flow.Flow

interface TransactionHistoryRepository {

    suspend fun saveTransaction(transaction: TransactionHistory)

    fun getAllTransactionHistory(): Flow<List<TransactionHistory>>

    fun getTransactionById(id: String): Flow<TransactionHistory?>

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionHistory>>

    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<TransactionHistory>>

    fun getTransactionsByProvider(provider: ProviderType): Flow<List<TransactionHistory>>
}