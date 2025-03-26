package com.chicohan.mobiletopup.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicohan.mobiletopup.helper.PreferencesHelper
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.domain.model.UIState
import com.chicohan.mobiletopup.domain.repository.TelecomRepository
import com.chicohan.mobiletopup.domain.useCases.UseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCases: UseCases,
    private val repository: TelecomRepository,
    preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val _baseCurrencyState = MutableStateFlow<UIState<TelecomProvider?>>(UIState.Idle)
    val baseCurrencyState = _baseCurrencyState.asStateFlow()

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

}