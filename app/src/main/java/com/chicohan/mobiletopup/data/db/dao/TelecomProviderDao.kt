package com.chicohan.mobiletopup.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import kotlinx.coroutines.flow.Flow


@Dao
interface TelecomProviderDao {

    @Query("SELECT * FROM telecom_providers")
    suspend fun getTelecomProviders(): List<TelecomProvider>

    @Query("SELECT * FROM telecom_providers")
    fun getTelecomProviderStream(): Flow<List<TelecomProvider>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(rates: List<TelecomProvider>)

    @Query("DELETE FROM telecom_providers")
    suspend fun clearProviders()

}