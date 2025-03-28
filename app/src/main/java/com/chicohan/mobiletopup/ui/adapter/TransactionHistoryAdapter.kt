package com.chicohan.mobiletopup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.chicohan.mobiletopup.R
import com.chicohan.mobiletopup.data.db.entity.ProviderType
import com.chicohan.mobiletopup.data.db.entity.TransactionHistory
import com.chicohan.mobiletopup.data.db.entity.getDetailTransactionTypeName
import com.chicohan.mobiletopup.data.db.entity.getName
import com.chicohan.mobiletopup.databinding.ItemTransactionHistoryBinding

class TransactionHistoryAdapter(
    private val glide: RequestManager,
    private val onItemClick: ((item: TransactionHistory) -> Unit)? = null,
) : ListAdapter<TransactionHistory, TransactionHistoryAdapter.MyListItemViewHolder>(
    ListDiffCallBack()
) {

    class ListDiffCallBack : DiffUtil.ItemCallback<TransactionHistory>() {
        override fun areItemsTheSame(
            oldItem: TransactionHistory,
            newItem: TransactionHistory
        ): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(
            oldItem: TransactionHistory,
            newItem: TransactionHistory
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class MyListItemViewHolder(val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListItemViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyListItemViewHolder, position: Int) {
        with(holder.binding) {
            val item = getItem(position)
            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
            tvProviderName.text = item.providerType.getName()
            tvPhoneNumber.text = item.phoneNumber
            tvAmount.text = item.getFormattedAmount()
            tvDate.text = item.getFormattedTransactionTime()
            chipStatus.text = item.status.name
            tvTransactionType.text =
                item.transactionType.getDetailTransactionTypeName(item.providerType)
            when (item.providerType) {
                ProviderType.ATOM -> animationState.setImageResource(R.drawable.atom_logo)
                ProviderType.MPT -> animationState.setImageResource(R.drawable.mpt_logo)
                ProviderType.OOREDOO -> animationState.setImageResource(R.drawable.ooredoo_logo)
                ProviderType.UNKNOWN -> Unit
            }
//            when (item.status) {
//                TransactionStatus.SUCCESS -> animationState.setAnimation(R.raw.lottie_payment_success)
//                TransactionStatus.PENDING -> Unit
//                TransactionStatus.FAILED -> animationState.setAnimation(R.raw.lottie_error_animation)
//
//            }
//            glide.load(item.pr).into(ivDataIcon)
        }
    }
}