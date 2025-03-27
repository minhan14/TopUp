package com.chicohan.mobiletopup.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.data.db.model.DataPlan
import com.chicohan.mobiletopup.domain.repository.TransactionHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    transactionHistoryRepository: TransactionHistoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<TransactionStatus?>(null)
    private val _selectedProvider = MutableStateFlow<ProviderType?>(null)

    val transactionHistoryList = combine(
        transactionHistoryRepository.getAllTransactionHistory(),
        _selectedStatus,
        _selectedProvider,
        _searchQuery
    ) { transactions, status, provider, query ->
        transactions.filter { transaction ->
            val matchesStatus = status == null || transaction.status == status
            val matchesProvider = provider == null || transaction.providerType == provider
            val matchesQuery = query.isEmpty() ||
                    transaction.phoneNumber?.contains(query, ignoreCase = true) == true ||
                    transaction.providerType.getName().contains(query, ignoreCase = true)

            matchesStatus && matchesProvider && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun setSearchQuery(query: String) = _searchQuery.update { query }
    fun setSelectedStatus(status: TransactionStatus?) = _selectedStatus.update { status }
    fun setSelectedProvider(provider: ProviderType?) = _selectedProvider.update { provider }

}