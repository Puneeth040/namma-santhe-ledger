package com.example.nammasantheledger.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.example.nammasantheledger.databinding.FragmentCustomerLedgerBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory
import com.example.nammasantheledger.ui.adapter.TransactionAdapter
import com.example.nammasantheledger.ui.util.asRupee

class CustomerLedgerFragment : Fragment() {
    private var _binding: FragmentCustomerLedgerBinding? = null
    private val binding get() = _binding!!
    private val adapter = TransactionAdapter()

    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerLedgerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val customerId = requireArguments().getLong("customerId")
        val customerName = requireArguments().getString("customerName").orEmpty()
        val phone = requireArguments().getString("phone").orEmpty()
        binding.tvCustomerName.text = customerName
        binding.rvTransactions.adapter = adapter
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())

        viewModel.customerLedger(customerId).observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvEmptyLedger.isVisible = list.isEmpty()
            val due = list.sumOf { if (it.type.name == "CREDIT") it.amount else -it.amount }
            binding.tvRunningBalance.text = due.coerceAtLeast(0.0).asRupee()
            binding.tvAdvance.text = (due * -1).coerceAtLeast(0.0).asRupee()
            binding.tvAdvance.visibility = if (due < 0) View.VISIBLE else View.GONE
            binding.tvAdvanceLabel.visibility = if (due < 0) View.VISIBLE else View.GONE
            binding.btnWhatsAppReminder.setOnClickListener {
                openWhatsApp(customerName, phone, due.coerceAtLeast(0.0))
            }
        }
    }

    private fun openWhatsApp(customerName: String, phone: String, due: Double) {
        val cleanPhone = phone.filter { it.isDigit() }
        if (cleanPhone.isBlank()) {
            Snackbar.make(binding.root, "Customer phone number not available.", Snackbar.LENGTH_SHORT).show()
            return
        }
        val message =
            "Namaskara $customerName, your pending Udari amount is ${due.asRupee()}. Kindly pay when possible. - Namma Santhe Ledger"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/91$cleanPhone?text=${Uri.encode(message)}")
            setPackage("com.whatsapp")
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Snackbar.make(binding.root, "WhatsApp is not installed.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
