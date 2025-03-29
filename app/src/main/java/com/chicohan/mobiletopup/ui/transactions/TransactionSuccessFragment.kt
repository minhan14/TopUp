package com.chicohan.mobiletopup.ui.transactions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.TransactionStatus
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.databinding.FragmentTransactionSuccessBinding

class TransactionSuccessFragment : Fragment(R.layout.fragment_transaction_success) {

    private val args by navArgs<TransactionSuccessFragmentArgs>()

    private lateinit var binding: FragmentTransactionSuccessBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionSuccessBinding.bind(view)
        setUpViews()
    }

    private fun setUpViews() = with(binding) {
        args.transactionDetailArgs?.let { transactionHistory ->
            when (transactionHistory.status) {
                TransactionStatus.SUCCESS -> {
                    "Transaction Successful!".also { txtTransactionStatus.text = it }
                    animationTransactionStatus.setAnimation(R.raw.lottie_payment_success)
                }

                TransactionStatus.PENDING -> Unit

                TransactionStatus.FAILED -> {
                    "Transaction Failed!".also { txtTransactionStatus.text = it }
                    animationTransactionStatus.setAnimation(R.raw.lottie_error_animation)
                }

            }
            tvTransactionId.text = transactionHistory.transactionId
            tvProviderName.text = transactionHistory.providerType.getName()
            tvTransactionType.text = transactionHistory.getDetailTransactionTypeName()
//                transactionHistory.transactionType.getDetailTransactionTypeName(transactionHistory.providerType)
            tvAmount.text = transactionHistory.getFormattedAmount()
            tvDate.text = transactionHistory.getFormattedTransactionTime()

        }
        btnDone.setOnClickListener{ findNavController().popBackStack() }
    }
}