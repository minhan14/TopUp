package com.chicohan.mobiletopup.domain.useCases

import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DetectProviderUseCase @Inject constructor(
    private val telecomRepository: TelecomRepository
) {
    suspend operator fun invoke(mobileNumber: String): TelecomProvider? {
        return telecomRepository.detectProviderFromNumber(mobileNumber)
    }
}