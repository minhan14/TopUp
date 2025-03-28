package com.chicohan.mobiletopup.ui.phoneNumber

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicohan.currencyexchange.domain.model.Event
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import com.chicohan.mobiletopup.domain.useCases.UseCases
import com.chicohan.mobiletopup.helper.PreferencesHelper
import com.chicohan.mobiletopup.helper.isValidPhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneNumberViewModel @Inject constructor(
    private val useCases: UseCases,
    private val preferencesHelper: PreferencesHelper,
    repository: TelecomRepository
) : ViewModel() {

    companion object {
        private const val PHONE_NUMBER_VM_TAG = "PhoneNumberViewModel"
    }

    var phNumberUiState = MutableStateFlow(PhoneNumberUiState()); private set

    var currentPhoneNumber = MutableStateFlow(preferencesHelper.getCurrentNumber()); private set

    init {
        /*
        Check if this is the first run
        for one time event , initialize default providers
         */
        viewModelScope.launch {
            if (preferencesHelper.isFirstRun()) {
                repository.initializeProviders()
                preferencesHelper.setFirstRunCompleted()
            }
        }

    }

    fun validatePhoneNumber(phoneNumber: String) = viewModelScope.launch {
        phNumberUiState.update { it.copy(loading = true) }
        val result = useCases.detectProviderUseCase(phoneNumber)

        result.fold(
            onSuccess = { provider ->
                Log.d(PHONE_NUMBER_VM_TAG, "Provider found: $provider")
                preferencesHelper.saveCurrentNumber(phoneNumber)
                currentPhoneNumber.update { phoneNumber }
                phNumberUiState.update {
                    it.copy(loading = false, isSuccess = Event(provider), errorMessage = Event(null))
                }
            },
            onFailure = { error ->
                phNumberUiState.update {
                    it.copy(loading = false, isSuccess = Event(null), errorMessage = Event(error.message))
                }
            }
        )

    }

    fun changePhoneNumber() = viewModelScope.launch {
        preferencesHelper.clearPhoneNumber()
        currentPhoneNumber.update { null }
    }
}

data class PhoneNumberUiState(
    val loading: Boolean = false,
    val isSuccess: Event<TelecomProvider?> = Event(null),
    val errorMessage: Event<String?> = Event(null),
)
