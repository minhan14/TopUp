package com.chicohan.mobiletopup.ui.phoneNumber

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.databinding.FragmentPhoneNumberBinding
import com.chicohan.mobiletopup.helper.collectFlowWithLifeCycleAtStateStart
import com.chicohan.mobiletopup.helper.toast
import com.chicohan.mobiletopup.helper.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneNumberFragment : Fragment(R.layout.fragment_phone_number) {
    lateinit var binding: FragmentPhoneNumberBinding; private set
    private val phoneNumberViewModel by activityViewModels<PhoneNumberViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPhoneNumberBinding.bind(view)
        collectFlowWithLifeCycleAtStateStart(phoneNumberViewModel.phNumberUiState) {
            handleUiState(it)
        }
    }

    private fun handleUiState(state: PhoneNumberUiState) = with(state) {
        with(binding) {
            buttonProceed.setOnClickListener {
                val phoneNumber = edtEnterPhoneNumber.text.toString()
                phoneNumberViewModel.validatePhoneNumber(phoneNumber = phoneNumber)
            }
            progressBar.visible(loading)
            errorMessage.getContentIfNotHandled()?.let {
                requireContext().toast(it)
            }
            isSuccess.getContentIfNotHandled()?.let { findNavController().popBackStack() }
        }
    }
}