package com.chicohan.mobiletopup.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chicohan.mobiletopup.MobileNavigationDirections
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionType
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.data.model.DataPlan
import com.chicohan.mobiletopup.data.model.RechargeData
import com.chicohan.mobiletopup.databinding.BottomSheetPaymentBinding
import com.chicohan.mobiletopup.domain.model.UIState
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateResume
import com.chicohan.mobiletopup.helper.toast
import com.chicohan.mobiletopup.helper.visible
import com.chicohan.mobiletopup.ui.main.MainViewModel
import com.chicohan.mobiletopup.ui.phoneNumber.PhoneNumberViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentBottomSheet(private val transactionType: TransactionType) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetPaymentBinding? = null
    private val binding get() = _binding!!

    private val phoneNumberViewModel: PhoneNumberViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPaymentBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlowWithLifeCycleAtStateResume(mainViewModel.confirmPaymentUiState) { uiState ->
            handlePaymentUiState(uiState)
        }
        setupConfirmButton()
        updateUI()

    }

    private fun setupConfirmButton() = with(binding) {
        val selectedPlanFlow = when (transactionType) {
            TransactionType.RECHARGE -> mainViewModel.selectedRechargePlan
            TransactionType.DATA_PACK -> mainViewModel.selectedDataPlan
        }

        collectFlowWithLifeCycleAtStateResume(selectedPlanFlow) { selectedPlan ->
            btnConfirmPayment.setOnClickListener {
                val provider = mainViewModel.currentProvider.value ?: return@setOnClickListener
                val phone =
                    phoneNumberViewModel.currentPhoneNumber.value ?: return@setOnClickListener
                val amount = when (selectedPlan) {
                    is RechargeData -> selectedPlan.amount
                    is DataPlan -> selectedPlan.amount
                    else -> 0.0
                }
                val transactionHistory = TransactionHistory(
                    phoneNumber = phone,
                    providerType = provider.type,
                    transactionType = transactionType,
                    amount = amount
                )
                mainViewModel.confirmPayment(transactionHistory)
            }
        }
    }

    private fun handlePaymentUiState(state: UIState<TransactionHistory>) = with(binding) {
        progressBar.visible(state is UIState.Loading)
        if (state is UIState.Success) {
            val action =
                MobileNavigationDirections.actionGlobalTransactionSuccessFragment(state.result)
            findNavController().navigate(action)
            clearStates()
            dismiss()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI() = with(binding) {
        val provider = mainViewModel.currentProvider.value ?: return
        val phone = phoneNumberViewModel.currentPhoneNumber.value ?: return
        tvProviderName.text = provider.type.getName()
        ivProviderIcon.setImageResource(provider.logoPath)
        val selectedPlan = when (transactionType) {
            TransactionType.RECHARGE -> mainViewModel.selectedRechargePlan.value
            TransactionType.DATA_PACK -> mainViewModel.selectedDataPlan.value
        }
        val amount = when (selectedPlan) {
            is RechargeData -> selectedPlan.getFormattedAmount()
            is DataPlan -> selectedPlan.getFormattedAmount()
            else -> "Loading"
        }
        val planType = when (selectedPlan) {
            is RechargeData -> "Recharge for ${selectedPlan.providerType.getName()}"
            is DataPlan -> selectedPlan.getFormattedDescription()
            else -> "Loading"
        }
        tvDataPlan.text = planType
        tvAmount.text = amount
        tvPhoneNumber.text = phone
    }


    override fun onDestroyView() {
        clearStates()
        super.onDestroyView()
        _binding = null
    }

    private fun clearStates() = with(mainViewModel) {
        resetPaymentUiState()
        resetSelectedPlans()
    }

    companion object {
        const val TAG = "PaymentBottomSheet"
    }

} 