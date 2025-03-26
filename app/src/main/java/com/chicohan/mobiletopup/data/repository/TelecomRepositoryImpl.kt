package com.chicohan.mobiletopup.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.chicohan.mobiletopup.data.db.dao.TelecomProviderDao
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

import com.chicohan.mobiletopup.R
import dagger.hilt.android.qualifiers.ApplicationContext

class TelecomRepositoryImpl @Inject constructor(
    private val telecomProviderDao: TelecomProviderDao
) : TelecomRepository {

    private val default = listOf(
        TelecomProvider(
            type = ProviderType.ATOM,
            logoPath = R.drawable.atom_logo,
            prefixes = listOf("79", "78")
        ),
        TelecomProvider(
            type = ProviderType.OOREDOO,
            logoPath = R.drawable.ooredoo_logo,
            prefixes = listOf("96", "97", "99")
        ),
        TelecomProvider(
            type = ProviderType.MPT,
            logoPath = R.drawable.mpt_logo,
            prefixes = listOf("40","42","44","45", "48", "49","89","88")
        )
    )

    override suspend fun detectProviderFromNumber(mobileNumber: String): TelecomProvider? {
        val providers = telecomProviderDao.getTelecomProviders()
        val numberWithoutCountryCode = mobileNumber.replace("+959", "").replace(" ", "").removePrefix("09")
        return providers.find { provider ->
            provider.prefixes.any { prefix -> numberWithoutCountryCode.startsWith(prefix) }
        }
    }

    override fun getTelecomProviders(): Flow<List<TelecomProvider>> =
        telecomProviderDao.getTelecomProviderStream()


    override suspend fun initializeProviders() {
        telecomProviderDao.insertProviders(default)
    }


}