package com.demo.assignmentapplication.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.assignmentapplication.R
import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.assignmentapplication.databinding.ItemHoldingBinding

class HoldingsAdapter :
    ListAdapter<HoldingEntity, HoldingsAdapter.HoldingViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldingViewHolder {
        val binding = ItemHoldingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HoldingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HoldingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class HoldingViewHolder(private val binding: ItemHoldingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(holding: HoldingEntity) {
            binding.tvSymbol.text = holding.symbol
            binding.tvLtp.text = " ₹ ${holding.ltp}"
            binding.tvQuantity.text = " ${holding.quantity}"

            val pnl = (holding.ltp - holding.avgPrice) * holding.quantity
            binding.tvPnl.text = " ₹ ${"%.2f".format(pnl)}"
            val pnlColor = if (pnl >= 0) {
                ContextCompat.getColor(binding.root.context, R.color.profit_green)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.loss_red)
            }
            binding.tvPnl.setTextColor(pnlColor)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HoldingEntity>() {
        override fun areItemsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity): Boolean =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: HoldingEntity, newItem: HoldingEntity): Boolean =
            oldItem == newItem
    }
}

