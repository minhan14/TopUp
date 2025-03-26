package com.chicohan.mobiletopup.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.TelecomProvider
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.databinding.FragmentMainBinding
import com.chicohan.mobiletopup.domain.model.UIState
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateStart
import com.chicohan.mobiletopup.ui.phoneNumber.PhoneNumberViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    lateinit var binding: FragmentMainBinding
        private set

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val phoneNumberViewModel by activityViewModels<PhoneNumberViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)
        collectFlowWithLifeCycleAtStateStart(phoneNumberViewModel.currentPhoneNumber) { currentPhoneNumber ->
            if (currentPhoneNumber.isNullOrBlank()) {
                findNavController().navigate(R.id.action_mainFragment_to_phoneNumberFragment)
            } else {
                binding.txtPhone.text = currentPhoneNumber
                phoneNumberViewModel.validatePhoneNumber(currentPhoneNumber)
            }
        }
        collectFlowWithLifeCycleAtStateStart(phoneNumberViewModel.phNumberUiState) { state ->
           with(state){
               isSuccess.getContentIfNotHandled()?.let {
                   binding.txtProvider.text = it.type.getName()
                   binding.ivProviderIcon.setImageResource(R.drawable.ooredoo_logo)
               }
           }
        }
    }
}