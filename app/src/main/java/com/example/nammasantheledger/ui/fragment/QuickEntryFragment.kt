package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.example.nammasantheledger.data.model.TransactionType
import com.example.nammasantheledger.databinding.FragmentQuickEntryBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory

class QuickEntryFragment : Fragment() {
    private var _binding: FragmentQuickEntryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    private var selectedCustomerId: Long? = null
    private var transactionType: TransactionType = TransactionType.CREDIT

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuickEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        transactionType = arguments?.getString("type")?.let { TransactionType.valueOf(it) } ?: TransactionType.CREDIT
        binding.tvTitle.text = if (transactionType == TransactionType.CREDIT) "Quick Credit Entry" else "Payment Entry"

        val amounts = listOf("50", "100", "200", "500")
        binding.chip50.text = "₹50"
        binding.chip100.text = "₹100"
        binding.chip200.text = "₹200"
        binding.chip500.text = "₹500"
        listOf(binding.chip50, binding.chip100, binding.chip200, binding.chip500).forEachIndexed { index, chip ->
            chip.setOnClickListener { binding.etAmount.setText(amounts[index]) }
        }

        viewModel.customers.observe(viewLifecycleOwner) { customers ->
            val names = customers.map { it.name }
            binding.actvCustomer.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
            )
            binding.actvCustomer.setOnItemClickListener { _, _, position, _ ->
                selectedCustomerId = customers[position].id
            }
        }

        binding.btnSave.setOnClickListener {
            val customerId = selectedCustomerId
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            if (customerId == null || amount == null || amount <= 0.0) {
                Toast.makeText(requireContext(), "Select customer and enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.btnSave.isEnabled = false
            val note = binding.etNote.text.toString().trim().ifBlank { null }
            if (transactionType == TransactionType.CREDIT) {
                viewModel.addCredit(customerId, amount, note)
            } else {
                viewModel.addPayment(customerId, amount, note)
            }
            Snackbar.make(binding.root, "Transaction saved", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
