package com.chicohan.mobiletopup.ui.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.chicohan.mobiletopup.MobileNavigationDirections
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.TransactionType
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.data.model.DataPlan
import com.chicohan.mobiletopup.data.model.RechargeData
import com.chicohan.mobiletopup.databinding.BottomSheetPaymentBinding
import com.chicohan.mobiletopup.domain.model.UIState
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateResume
import com.chicohan.mobiletopup.helper.logo
import com.chicohan.mobiletopup.helper.serializable
import com.chicohan.mobiletopup.helper.toast
import com.chicohan.mobiletopup.helper.viewBinding
import com.chicohan.mobiletopup.helper.visible
import com.chicohan.mobiletopup.ui.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentBottomSheet : BottomSheetDialogFragment() {

    private val binding: BottomSheetPaymentBinding by viewBinding(BottomSheetPaymentBinding::inflate)

    private val mainViewModel: MainViewModel by activityViewModels()

    private val transactionType: TransactionType? by lazy {
        arguments?.serializable(
            ARG_TRANSACTION_TYPE
        )
    }
    private val phoneNumber: String? by lazy { arguments?.getString(ARG_PHONE_NUMBER) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updatePaymentDetails()
        collectFlowWithLifeCycleAtStateResume(mainViewModel.confirmPaymentUiState) { state ->
            handlePaymentState(state)
        }
    }

    private fun handlePaymentState(state: UIState<TransactionHistory>) = with(binding) {
        btnDismiss.setOnClickListener {
            if (state is UIState.Loading) {
                requireContext().toast("Transaction in process, Please Wait!!!")
                return@setOnClickListener
            }
            dismiss()
        }
        progressBar.visible(state is UIState.Loading)
        if (state is UIState.Success) {
            val action =
                MobileNavigationDirections.actionGlobalTransactionSuccessFragment(state.result)
            findNavController().navigate(action)
            clearStates()
            dismiss()
        }
    }

    private fun getSelectedPlan() = when (transactionType) {
        TransactionType.RECHARGE -> mainViewModel.selectedRechargePlan.value as Any
        TransactionType.DATA_PACK -> mainViewModel.selectedDataPlan.value as Any
        else -> null
    }

    @SuppressLint("SetTextI18n")
    private fun updatePaymentDetails() = with(binding) {
        val provider = mainViewModel.currentProvider.value ?: return
        val selectedPlan = getSelectedPlan()

        tvProviderName.text = provider.type.getName()
        val logo = provider.type.logo
        ivProviderIcon.setImageResource(logo)
        tvPhoneNumber.text = phoneNumber ?: "Loading"

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

        btnConfirmPayment.setOnClickListener { handlePaymentConfirmation() }
    }

    private fun handlePaymentConfirmation() {
        mainViewModel.currentProvider.value?.let { provider ->
            val amount = when (val selectedPlan = getSelectedPlan()) {
                is RechargeData -> selectedPlan.amount
                is DataPlan -> selectedPlan.amount
                else -> 0.0
            }

            transactionType?.let { type ->
                TransactionHistory(
                    phoneNumber = phoneNumber ?: "",
                    providerType = provider.type,
                    transactionType = type,
                    amount = amount
                ).also { history ->
                    mainViewModel.confirmPayment(history)
                }
            }
        }
    }

    private fun clearStates() = with(mainViewModel) {
        resetPaymentUiState();resetSelectedDataPlans();resetSelectedRechargePlan()
    }

    companion object {
        private const val ARG_TRANSACTION_TYPE = "transactionType"
        private const val ARG_PHONE_NUMBER = "phoneNumber"
        const val TAG = "PaymentBottomSheet"
        fun newInstance(transactionType: TransactionType, phoneNumber: String): PaymentBottomSheet {
            return PaymentBottomSheet().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_TRANSACTION_TYPE, transactionType)
                    putString(ARG_PHONE_NUMBER, phoneNumber)
                }
            }
        }


    }
}

