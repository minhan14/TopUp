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
import com.chicohan.mobiletopup.data.db.model.DataPlan
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        updateUI()

    }

    private fun setupViews() = with(binding) {
        collectFlowWithLifeCycleAtStateResume(mainViewModel.selectedDataPlan) { selectedPlan ->
            btnConfirmPayment.setOnClickListener {
                val provider = mainViewModel.currentProvider.value ?: return@setOnClickListener
                val phone =
                    phoneNumberViewModel.currentPhoneNumber.value ?: return@setOnClickListener

                val transactionHistory = TransactionHistory(
                    phoneNumber = phone,
                    providerType = provider.type,
                    transactionType = transactionType,
                    amount = selectedPlan?.amount ?: 0.0,
                )
                mainViewModel.confirmPayment(transactionHistory)

            }
        }

        collectFlowWithLifeCycleAtStateResume(mainViewModel.confirmPaymentUiState) { uiState ->
            handlePaymentUiState(uiState)
        }

    }

    private fun handlePaymentUiState(state: UIState<TransactionHistory>) = with(binding) {
        progressBar.visible(state is UIState.Loading)
        if (state is UIState.Success) {
            val action =
                MobileNavigationDirections.actionGlobalTransactionSuccessFragment(state.result)
            findNavController().navigate(action)
            mainViewModel.resetPaymentUiState()
            dismiss()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI() = with(binding) {
        val provider = mainViewModel.currentProvider.value ?: return
        val phone = phoneNumberViewModel.currentPhoneNumber.value ?: return
        tvProviderName.text = provider.type.getName()
        ivProviderIcon.setImageResource(provider.logoPath)
        val selectedPlan = mainViewModel.selectedDataPlan.value
        tvDataPlan.text = selectedPlan?.getFormattedDescription() ?: "Loading"
        tvAmount.text = selectedPlan?.getFormattedAmount() ?: "Loading"
        tvPhoneNumber.text = phone
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PaymentBottomSheet"
    }

} 