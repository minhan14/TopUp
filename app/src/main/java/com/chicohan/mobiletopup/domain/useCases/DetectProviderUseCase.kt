package com.chicohan.mobiletopup.domain.useCases

import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import com.chicohan.mobiletopup.helper.isValidPhoneNumber
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DetectProviderUseCase @Inject constructor(
    private val telecomRepository: TelecomRepository
) {
    suspend operator fun invoke(mobileNumber: String): Result<TelecomProvider> {
        if (mobileNumber.isEmpty() or mobileNumber.isBlank()) {
            return Result.failure(Exception("Phone number can not be empty!!"))
        }
        if (!mobileNumber.isValidPhoneNumber()) {
            return Result.failure(Exception("Please provide phone number in correct format!!"))
        }
        return runCatching {
            telecomRepository.detectProviderFromNumber(mobileNumber)
                ?: throw Exception("Cannot find the provider for this phone number!!!")
        }

    }
}