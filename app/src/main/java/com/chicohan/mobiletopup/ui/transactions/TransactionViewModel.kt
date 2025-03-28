package com.chicohan.mobiletopup.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.domain.repository.TransactionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionHistoryRepository: TransactionHistoryRepository
) : ViewModel() {

    private val _isDateFilterExpanded = MutableStateFlow(false)
    val isDateFilterExpanded: StateFlow<Boolean> = _isDateFilterExpanded

    var filters = MutableStateFlow(TransactionFilters())
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    private val transactionsFlow: Flow<List<TransactionHistory>> = filters
        .map { it.startDate to it.endDate }
        .distinctUntilChanged()
        .flatMapLatest { (startDate, endDate) ->
            if (startDate != null && endDate != null) {
                transactionHistoryRepository.getTransactionsByDateRange(startDate, endDate)
            } else {
                transactionHistoryRepository.getAllTransactionHistory()
            }
        }

    val transactionHistoryList: StateFlow<List<TransactionHistory>> = combine(
        transactionsFlow,
        filters
    ) { transactions, activeFilters ->
        transactions.filter { transaction ->
            val matchesStatus = activeFilters.selectedStatus == null ||
                    transaction.status == activeFilters.selectedStatus

            val matchesProvider = activeFilters.selectedProvider == null ||
                    transaction.providerType == activeFilters.selectedProvider

            val matchesQuery = activeFilters.searchQuery.isEmpty() ||
                    transaction.phoneNumber?.contains(
                        activeFilters.searchQuery,
                        ignoreCase = true
                    ) == true ||
                    transaction.providerType.getName()
                        .contains(activeFilters.searchQuery, ignoreCase = true)

            matchesStatus && matchesProvider && matchesQuery
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun setSearchQuery(query: String) = filters.update { it.copy(searchQuery = query) }

    fun setSelectedStatus(status: TransactionStatus?) = filters.update { it.copy(selectedStatus = status) }

    fun setSelectedProvider(provider: ProviderType?) = filters.update { it.copy(selectedProvider = provider) }

    fun setStartDates(startDate: Long?) = filters.update { it.copy(startDate = startDate) }

    fun setEndDates(endDate: Long?) = filters.update { it.copy(endDate = endDate) }

    fun clearDateFilter() = filters.update { it.copy(startDate = null, endDate = null) }

    fun setDateFilterExpanded(expanded: Boolean) = _isDateFilterExpanded.update { expanded }

}

data class TransactionFilters(
    val searchQuery: String = "",
    val selectedStatus: TransactionStatus? = null,
    val selectedProvider: ProviderType? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

