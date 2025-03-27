package com.chicohan.mobiletopup.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionHistory)

    @Query("SELECT * FROM transaction_history ORDER BY transactionTime DESC")
    fun getAllTransactions(): Flow<List<TransactionHistory>>

    @Query("SELECT * FROM transaction_history WHERE transactionId = :id")
    fun getTransactionById(id: String): Flow<TransactionHistory?>

    @Query("SELECT * FROM transaction_history WHERE transactionTime BETWEEN :startDate AND :endDate ORDER BY transactionTime DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionHistory>>

    @Query("SELECT * FROM transaction_history WHERE status = :status ORDER BY transactionTime DESC")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<TransactionHistory>>

    @Query("SELECT * FROM transaction_history WHERE providerType = :provider ORDER BY transactionTime DESC")
    fun getTransactionsByProvider(provider: ProviderType): Flow<List<TransactionHistory>>

}