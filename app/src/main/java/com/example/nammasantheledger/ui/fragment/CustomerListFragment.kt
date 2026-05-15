package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nammasantheledger.R
import com.example.nammasantheledger.data.model.CustomerEntity
import com.example.nammasantheledger.databinding.FragmentCustomerListBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory
import com.example.nammasantheledger.ui.adapter.CustomerAdapter

class CustomerListFragment : Fragment() {
    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    private val adapter = CustomerAdapter(
        onClick = { openLedger(it) },
        onLongClick = { showOptions(it) }
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressBar.isVisible = true
        binding.rvCustomers.adapter = adapter
        binding.rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        binding.etSearch.addTextChangedListener { viewModel.setCustomerQuery(it.toString()) }
        binding.fabAddCustomer.setOnClickListener {
            findNavController().navigate(R.id.addEditCustomerFragment)
        }
        viewModel.customers.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.progressBar.isVisible = false
            binding.tvEmptyCustomers.isVisible = it.isEmpty()
        }
        setupToolbarSearch()
    }

    private fun setupToolbarSearch() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.customer_search_menu, menu)
                val searchView = menu.findItem(R.id.action_search).actionView as SearchView
                searchView.queryHint = getString(R.string.search_customer)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.setCustomerQuery(newText.orEmpty())
                        binding.etSearch.setText(newText.orEmpty())
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem): Boolean = false
        }
        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun openLedger(customer: CustomerEntity) {
        findNavController().navigate(
            R.id.customerLedgerFragment,
            bundleOf("customerId" to customer.id, "customerName" to customer.name, "phone" to customer.phone)
        )
    }

    private fun showOptions(customer: CustomerEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle(customer.name)
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                if (which == 0) {
                    findNavController().navigate(
                        R.id.addEditCustomerFragment,
                        bundleOf("customerId" to customer.id, "name" to customer.name, "phone" to customer.phone, "address" to customer.address)
                    )
                } else {
                    viewModel.deleteCustomer(customer)
                }
            }.show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
