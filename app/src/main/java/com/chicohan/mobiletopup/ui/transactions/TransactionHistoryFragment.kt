package com.chicohan.mobiletopup.ui.transactions

import android.app.DatePickerDialog
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
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateStart
import com.chicohan.mobiletopup.helper.toFormattedDate
import com.chicohan.mobiletopup.helper.visible
import com.chicohan.mobiletopup.ui.adapter.TransactionHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.withContext
import java.util.Calendar
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

    private lateinit var binding: FragmentTransactionHistoryBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionHistoryBinding.bind(view)
        initAdapter()
        setupSearchBar()
        setupFilterChips()
        setupDateRangePicker()
        observeUIState()
    }

    private fun observeUIState() {

        collectFlowWithLifeCycleAtStateStart(transactionViewModel.isDateFilterExpanded) { isExpanded ->
            binding.dateFilterContainer.visible(isExpanded)
            binding.btnToggleDateFilter.setIconResource(
                if (isExpanded) R.drawable.baseline_keyboard_arrow_up_24
                else R.drawable.baseline_keyboard_arrow_down_24
            )
        }
        collectFlowWithLifeCycleAtStateStart(transactionViewModel.filters) { filters ->
            val startDate = filters.startDate?.toFormattedDate()
            val endDate = filters.endDate?.toFormattedDate()
            binding.tvStartDate.text = startDate ?: getString(R.string.select)
            binding.tvEndDate.text = endDate ?: getString(R.string.select)
            binding.btnClearDateFilter.visible(endDate != null && startDate != null)
        }
        collectFlowWithLifeCycleAtStateStart(transactionViewModel.transactionHistoryList) {
            transactionAdapter.submitList(it)
            binding.tvEmptyState.visible(it.isEmpty())
        }
    }

    private fun initAdapter() = with(binding.rvTransactions) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = transactionAdapter
    }

    private fun setupSearchBar() = with(binding.searchInput) {
        doAfterTextChanged {
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

    private fun setupDateRangePicker() = with(binding) {
        btnToggleDateFilter.setOnClickListener {
            val newState = !transactionViewModel.isDateFilterExpanded.value
            transactionViewModel.setDateFilterExpanded(newState)
        }
        startDateContainer.setOnClickListener {
            showDatePicker(true)
        }
        endDateContainer.setOnClickListener {
            showDatePicker(false)
        }
        btnClearDateFilter.setOnClickListener {
            clearDateFilter()
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.apply {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    set(year, month, dayOfMonth)

                    if (isStartDate) {
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        transactionViewModel.setStartDates(calendar.timeInMillis)

                    } else {
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        transactionViewModel.setEndDates(calendar.timeInMillis)
                    }
                },
                get(Calendar.YEAR),
                get(Calendar.MONTH),
                get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }


    private fun clearDateFilter() = transactionViewModel.clearDateFilter()

}