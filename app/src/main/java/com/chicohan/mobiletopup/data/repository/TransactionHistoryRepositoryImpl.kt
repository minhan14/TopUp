package com.chicohan.mobiletopup.data.repository

import com.chicohan.mobiletopup.data.db.dao.TransactionHistoryDao
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.domain.repository.TransactionHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionHistoryRepositoryImpl @Inject constructor(
    private val dao: TransactionHistoryDao
) : TransactionHistoryRepository {

    override suspend fun saveTransaction(transaction: TransactionHistory) =
        dao.insertTransaction(transaction)

    override fun getAllTransactionHistory(): Flow<List<TransactionHistory>> =
        dao.getAllTransactions()

    override fun getTransactionById(id: String): Flow<TransactionHistory?> =
        dao.getTransactionById(id)

    override fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionHistory>> = dao.getTransactionsByDateRange(startDate, endDate)


    override fun getTransactionsByStatus(status: TransactionStatus): Flow<List<TransactionHistory>> =
        dao.getTransactionsByStatus(status)

    override fun getTransactionsByProvider(provider: ProviderType): Flow<List<TransactionHistory>> =
        dao.getTransactionsByProvider(provider)

}