package com.chicohan.mobiletopup.domain.useCases

import javax.inject.Inject

data class UseCases @Inject constructor(
    val detectProviderUseCase: DetectProviderUseCase
)
