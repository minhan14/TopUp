package com.chicohan.mobiletopup.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.data.model.DataPlan
import com.chicohan.mobiletopup.data.model.RechargeData
import com.chicohan.mobiletopup.domain.model.UIState
import com.chicohan.mobiletopup.domain.repository.DataPlanRepository
import com.chicohan.mobiletopup.domain.repository.TransactionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataPlanRepository: DataPlanRepository,
    private val transactionHistoryRepository: TransactionHistoryRepository
) : ViewModel() {

    var currentProvider = MutableStateFlow<TelecomProvider?>(null); private set
    var selectedDataPlan = MutableStateFlow<DataPlan?>(null); private set
    var selectedRechargePlan = MutableStateFlow<RechargeData?>(null); private set


    private val _confirmPaymentUiState = MutableStateFlow<UIState<TransactionHistory>>(UIState.Idle)
    val confirmPaymentUiState = _confirmPaymentUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentDataPlans = currentProvider.flatMapLatest { provider ->
        provider?.let {
            dataPlanRepository.getDataPlansByProvider(it)
        } ?: flowOf(emptyList())
    }.map { dataPlans -> dataPlans.sortedBy { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentRechargePlans = currentProvider.flatMapLatest { provider ->
        provider?.let {
            dataPlanRepository.getRechargePlanByProvider(it)
        } ?: flowOf(emptyList())
    }.map { dataPlans -> dataPlans.sortedBy { it.amount } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun confirmPayment(transactionHistory: TransactionHistory) = viewModelScope.launch {
        _confirmPaymentUiState.update { UIState.Loading }
        val pendingTransaction = transactionHistory.copy(status = TransactionStatus.PENDING)
        transactionHistoryRepository.saveTransaction(pendingTransaction)
        delay(2000L)
        val errorTransaction = transactionHistory.copy(status = TransactionStatus.FAILED)
        val successTransaction = transactionHistory.copy(status = TransactionStatus.SUCCESS)
        val finalStatus = if ((0..1).random() == 0) successTransaction else errorTransaction
        transactionHistoryRepository.saveTransaction(finalStatus)
        _confirmPaymentUiState.update {
            UIState.Success(finalStatus)
        }
    }

    fun setTelecomProvider(provider: TelecomProvider) = currentProvider.update { provider }
    fun setSelectedDataPlan(item: DataPlan) = selectedDataPlan.update { item }
    fun setSelectedRechargePlan(item: RechargeData?) = selectedRechargePlan.update { item }
    fun resetSelectedPlans() = selectedDataPlan.update { null }
    fun resetPaymentUiState() = _confirmPaymentUiState.update { UIState.Idle }

}