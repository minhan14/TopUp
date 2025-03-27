package com.chicohan.mobiletopup.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.chicohan.mobiletopup.MobileNavigationDirections
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.databinding.FragmentTransactionHistoryBinding
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateResume
import com.chicohan.mobiletopup.helper.visible
import com.chicohan.mobiletopup.ui.adapter.TransactionHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionHistoryFragment : Fragment(R.layout.fragment_transaction_history) {

    private val transactionViewModel by viewModels<TransactionViewModel>()

    @Inject
    lateinit var glide: RequestManager

    private val transactionAdapter by lazy {
        TransactionHistoryAdapter(glide) { item: TransactionHistory ->
            val action = MobileNavigationDirections.actionGlobalTransactionSuccessFragment(item)
            findNavController().navigate(action)
        }
    }

    lateinit var binding: FragmentTransactionHistoryBinding; private set

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionHistoryBinding.bind(view)
        initViews()
        setupSearchBar()
        setupFilterChips()
        observeTransactions()
    }

    private fun initViews() = with(binding) {
        rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun setupSearchBar() = with(binding) {
        searchInput.doAfterTextChanged {
            transactionViewModel.setSearchQuery(it.toString())
        }
    }

    private fun setupFilterChips() = with(binding) {
        filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedStatus = when {
                chipSuccess.id in checkedIds -> TransactionStatus.SUCCESS
                chipPending.id in checkedIds -> TransactionStatus.PENDING
                chipFailed.id in checkedIds -> TransactionStatus.FAILED
                else -> null
            }
            transactionViewModel.setSelectedStatus(selectedStatus)
        }
        providerChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val selectedProvider = when {
                chipOoredoo.id in checkedIds -> ProviderType.OOREDOO
                chipMPT.id in checkedIds -> ProviderType.MPT
                chipAtom.id in checkedIds -> ProviderType.ATOM
                else -> null
            }
            transactionViewModel.setSelectedProvider(selectedProvider)
        }
        chipAll.isChecked = true
    }


    private fun observeTransactions() {
        collectFlowWithLifeCycleAtStateResume(transactionViewModel.transactionHistoryList) {
            transactionAdapter.submitList(it)
            binding.tvEmptyState.visible(it.isEmpty())
        }
    }
}