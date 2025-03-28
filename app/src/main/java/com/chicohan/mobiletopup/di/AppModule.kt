package com.chicohan.mobiletopup.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.AppDatabase
import com.chicohan.mobiletopup.data.db.dao.TelecomProviderDao
import com.chicohan.mobiletopup.data.db.dao.TransactionHistoryDao
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTelecomDao(database: AppDatabase): TelecomProviderDao {
        return database.telecomProviderDao()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionHistoryDao {
        return database.transactionHistoryDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )


    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

}