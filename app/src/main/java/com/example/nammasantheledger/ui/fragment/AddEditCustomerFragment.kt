package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.example.nammasantheledger.databinding.FragmentAddEditCustomerBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory

class AddEditCustomerFragment : Fragment() {
    private var _binding: FragmentAddEditCustomerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val customerId = arguments?.getLong("customerId", 0L)?.takeIf { it > 0L }
        binding.etName.setText(arguments?.getString("name").orEmpty())
        binding.etPhone.setText(arguments?.getString("phone").orEmpty())
        binding.etAddress.setText(arguments?.getString("address").orEmpty())

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            if (name.isBlank() || phone.isBlank()) {
                if (name.isBlank()) binding.tilName.error = "Required" else binding.tilName.error = null
                if (phone.isBlank()) binding.tilPhone.error = "Required" else binding.tilPhone.error = null
                return@setOnClickListener
            }
            binding.tilName.error = null
            binding.tilPhone.error = null
            viewModel.saveCustomer(customerId, name, phone, address.ifBlank { null })
            Snackbar.make(binding.root, "Customer saved successfully", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
