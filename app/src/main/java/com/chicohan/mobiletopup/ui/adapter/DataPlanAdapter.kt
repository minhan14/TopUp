package com.chicohan.mobiletopup.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.chicohan.mobiletopup.data.db.model.DataPlan
import com.chicohan.mobiletopup.databinding.ItemDataPlanBinding

class DataPlanAdapter(
    private val glide: RequestManager,
    private val onDataPlanClick: ((item: DataPlan) -> Unit)? = null,
) : ListAdapter<DataPlan, DataPlanAdapter.MyListItemViewHolder>(
    ListDiffCallBack()
) {
    class ListDiffCallBack : DiffUtil.ItemCallback<DataPlan>() {
        override fun areItemsTheSame(
            oldItem: DataPlan,
            newItem: DataPlan
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: DataPlan,
            newItem: DataPlan
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class MyListItemViewHolder(val binding: ItemDataPlanBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListItemViewHolder {
        val binding = ItemDataPlanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MyListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyListItemViewHolder, position: Int) {
        with(holder.binding) {
            val itemDataPlan = getItem(position)
            btnSelect.setOnClickListener {
                onDataPlanClick?.invoke(itemDataPlan)
            }
            txtDuration.text = itemDataPlan.getFormattedDuration()
            txtAmount.text = itemDataPlan.getFormattedAmount()
            txtDescription.text = itemDataPlan.getFormattedDescription()
            glide.load(itemDataPlan.iconResId).into(ivDataIcon)
        }
    }
}