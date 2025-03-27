package com.chicohan.mobiletopup.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.TransactionType
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.data.db.model.DataPlan
import com.chicohan.mobiletopup.databinding.BottomSheetPaymentBinding
import com.chicohan.mobiletopup.databinding.FragmentMainBinding
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateStart
import com.chicohan.mobiletopup.helper.toast
import com.chicohan.mobiletopup.ui.adapter.DataPlanAdapter
import com.chicohan.mobiletopup.ui.payment.PaymentBottomSheet
import com.chicohan.mobiletopup.ui.phoneNumber.PhoneNumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var binding: FragmentMainBinding
        private set

    @Inject
    lateinit var glide: RequestManager

    private val dataPlanAdapter by lazy {
        DataPlanAdapter(glide) { item ->
            showPaymentBottomSheet(item, TransactionType.DATA_PACK)
        }
    }

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val phoneNumberViewModel by activityViewModels<PhoneNumberViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        initViews()
        observePhoneNumber()
        observeProviderInfo()
        observeDataPlans()
    }

    private fun observePhoneNumber() =
        collectFlowWithLifeCycleAtStateStart(phoneNumberViewModel.currentPhoneNumber) { currentPhoneNumber ->
            if (currentPhoneNumber.isNullOrBlank()) {
                findNavController().navigate(R.id.action_mainFragment_to_phoneNumberFragment)
            } else {
                binding.txtPhone.text = currentPhoneNumber
                phoneNumberViewModel.validatePhoneNumber(currentPhoneNumber)
            }
        }

    private fun observeProviderInfo() =
        collectFlowWithLifeCycleAtStateStart(phoneNumberViewModel.phNumberUiState) { state ->
            with(state) {
                isSuccess.getContentIfNotHandled()?.let { providerInfo ->
                    Log.d(
                        "ProviderInfo",
                        "Provider: ${providerInfo.type.getName()}, LogoPath: ${providerInfo.logoPath}"
                    )
                    binding.txtProvider.text = providerInfo.type.getName()
                    binding.ivProviderIcon.setImageResource(providerInfo.logoPath)
                    mainViewModel.setTelecomProvider(providerInfo)
                }
            }
        }

    private fun observeDataPlans() =
        collectFlowWithLifeCycleAtStateStart(mainViewModel.currentDataPlans) {
            dataPlanAdapter.submitList(it)
        }

    private fun showPaymentBottomSheet(dataPlan: DataPlan, transactionType: TransactionType) {
        mainViewModel.selectedDataPlan(dataPlan)
        PaymentBottomSheet(
            transactionType = transactionType
        ).show(childFragmentManager, PaymentBottomSheet.TAG)

    }

    private fun handlePaymentConfirmation(dataPlan: DataPlan) {
        // Navigate to success screen with transaction details
//        val action = MainFragmentDirections.actionMainFragmentToTransactionSuccessFragment(
//            transactionId = generateTransactionId(),
//            providerName = mainViewModel.currentProvider.value?.getName() ?: "",
//            dataPlan = "${dataPlan.dataAmount} for ${dataPlan.validityDays} Days",
//            amount = "${dataPlan.amount} MMK",
//            phoneNumber = phoneNumberViewModel.currentPhoneNumber.value ?: ""
//        )
        findNavController().navigate(R.id.action_global_transactionSuccessFragment)
    }

    private fun generateTransactionId(): String {
        return "TXN${System.currentTimeMillis()}"
    }

    private fun initViews() = with(binding) {
        rvDataPlans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataPlanAdapter
        }
        animationTreeView.setOnClickListener {
            phoneNumberViewModel.changePhoneNumber()
        }
        btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_transactionHistoryFragment)
        }
    }
}