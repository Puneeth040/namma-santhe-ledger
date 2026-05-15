package com.example.nammasantheledger.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nammasantheledger.data.model.CustomerEntity
import com.example.nammasantheledger.databinding.ItemCustomerBinding

class CustomerAdapter(
    private val onClick: (CustomerEntity) -> Unit,
    private val onLongClick: (CustomerEntity) -> Unit
) : ListAdapter<CustomerEntity, CustomerAdapter.CustomerViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) = holder.bind(getItem(position))

    inner class CustomerViewHolder(private val binding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomerEntity) {
            binding.tvName.text = item.name
            binding.tvPhone.text = item.phone
            binding.tvAddress.text = item.address ?: "-"
            binding.root.setOnClickListener { onClick(item) }
            binding.btnLedger.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener {
                onLongClick(item)
                true
            }
        }
    }

    companion object {
        private val Diff = object : DiffUtil.ItemCallback<CustomerEntity>() {
            override fun areItemsTheSame(oldItem: CustomerEntity, newItem: CustomerEntity): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CustomerEntity, newItem: CustomerEntity): Boolean = oldItem == newItem
        }
    }
}
