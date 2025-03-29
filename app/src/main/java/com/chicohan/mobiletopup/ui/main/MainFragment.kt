package com.chicohan.mobiletopup.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionType
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.databinding.FragmentMainBinding
import com.chicohan.mobiletopup.databinding.ItemChipsBinding
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateResume
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateStart
import com.chicohan.mobiletopup.helper.logo
import com.chicohan.mobiletopup.helper.toast
import com.chicohan.mobiletopup.ui.adapter.DataPlanAdapter
import com.chicohan.mobiletopup.ui.payment.PaymentBottomSheet
import com.chicohan.mobiletopup.ui.phoneNumber.PhoneNumberViewModel
import com.google.android.material.chip.Chip
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
            mainViewModel.setSelectedDataPlan(item)
            showPaymentBottomSheet(TransactionType.DATA_PACK)
        }
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val phoneNumberViewModel by activityViewModels<PhoneNumberViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        initViews()
        observeRechargePlans()
        observePhoneNumber()
        observeProviderInfo()
        observeDataPlans()
    }

    private fun observeRechargePlans() = with(binding) {
        collectFlowWithLifeCycleAtStateStart(mainViewModel.currentRechargePlans) { rechargePlans ->
            Log.d("chip", "collected $rechargePlans")
            rechargeGroup.removeAllViews()
            rechargePlans.forEachIndexed { _, item ->
                val selected = mainViewModel.selectedRechargePlan.value
                val chip = ItemChipsBinding.inflate(layoutInflater, rechargeGroup, false).root as Chip
                chip.apply {
                    text = item.getFormattedAmount()
                    isCheckable = true
                    isChecked = (item == selected)
                    setOnClickListener {
                        mainViewModel.setSelectedRechargePlan(if (isChecked) item else null)
                    }
                    rechargeGroup.addView(this)
                }
            }
        }
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
                    val logo = providerInfo.type.logo
                    glide.load(logo).into(binding.ivProviderIcon)
//                    glide.load(providerInfo.logoPath).into(binding.ivProviderIcon)
                    mainViewModel.setTelecomProvider(providerInfo)
                }
            }
        }

    private fun observeDataPlans() =
        collectFlowWithLifeCycleAtStateStart(mainViewModel.currentDataPlans) {
            dataPlanAdapter.submitList(it)
        }

    private fun showPaymentBottomSheet(transactionType: TransactionType) {
        val phone = phoneNumberViewModel.currentPhoneNumber.value ?: return
        PaymentBottomSheet.newInstance(transactionType, phone)
            .show(childFragmentManager, PaymentBottomSheet.TAG)
    }

    private fun initViews() = with(binding) {
        rvDataPlans.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataPlanAdapter
        }
        btnResetPhoneNumber.setOnClickListener {
            mainViewModel.resetSelectedRechargePlan()
            mainViewModel.resetSelectedDataPlans()
            phoneNumberViewModel.changePhoneNumber()
        }
        btnHistory.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_transactionHistoryFragment)
        }
        btnRecharge.setOnClickListener {
            val currentRechargePlan = mainViewModel.selectedRechargePlan.value
            currentRechargePlan?.let {
                showPaymentBottomSheet(TransactionType.RECHARGE)
            } ?: run { requireContext().toast("Please Select a recharge Plan") }
        }
    }
}