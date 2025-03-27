package com.chicohan.mobiletopup.di

import android.content.Context
import com.chicohan.mobiletopup.data.repository.DataPlanRepositoryImpl
import com.chicohan.mobiletopup.data.repository.TelecomRepositoryImpl
import com.chicohan.mobiletopup.data.repository.TransactionHistoryRepositoryImpl
import com.chicohan.mobiletopup.domain.repository.DataPlanRepository
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import com.chicohan.mobiletopup.domain.repository.TransactionHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTelecomRepository(
        telecomRepositoryImpl: TelecomRepositoryImpl
    ): TelecomRepository

    @Binds
    @Singleton
    abstract fun bindDataPlanRepository(
        dataPlanRepositoryImpl: DataPlanRepositoryImpl
    ): DataPlanRepository


    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionHistoryRepositoryImpl
    ): TransactionHistoryRepository
}