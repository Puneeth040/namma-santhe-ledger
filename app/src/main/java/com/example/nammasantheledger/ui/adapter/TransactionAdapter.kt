package com.example.nammasantheledger.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nammasantheledger.R
import com.example.nammasantheledger.data.model.LedgerTransactionWithCustomer
import com.example.nammasantheledger.data.model.TransactionType
import com.example.nammasantheledger.databinding.ItemTransactionBinding
import com.example.nammasantheledger.ui.util.asDateTime
import com.example.nammasantheledger.ui.util.asRupee

class TransactionAdapter : ListAdapter<LedgerTransactionWithCustomer, TransactionAdapter.TxViewHolder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TxViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TxViewHolder, position: Int) = holder.bind(getItem(position))

    inner class TxViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LedgerTransactionWithCustomer) {
            binding.tvCustomerName.text = item.customerName
            binding.tvAmount.text = item.amount.asRupee()
            binding.tvDate.text = item.timestamp.asDateTime()
            binding.tvType.text = item.type.name
            binding.tvNote.text = item.note ?: "No note"
            val color = if (item.type == TransactionType.CREDIT) R.color.credit_red else R.color.payment_green
            binding.tvType.setTextColor(ContextCompat.getColor(binding.root.context, color))
            binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, color))
        }
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<LedgerTransactionWithCustomer>() {
            override fun areItemsTheSame(oldItem: LedgerTransactionWithCustomer, newItem: LedgerTransactionWithCustomer): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: LedgerTransactionWithCustomer, newItem: LedgerTransactionWithCustomer): Boolean = oldItem == newItem
        }
    }
}
