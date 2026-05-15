package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.view.isVisible
import com.example.nammasantheledger.databinding.FragmentHomeDashboardBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory
import com.example.nammasantheledger.ui.adapter.TransactionAdapter
import com.example.nammasantheledger.ui.util.asRupee

class HomeDashboardFragment : Fragment() {
    private var _binding: FragmentHomeDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    private val txAdapter = TransactionAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvRecentTransactions.adapter = txAdapter
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.progressBar.isVisible = true

        viewModel.safeOutstanding.observe(viewLifecycleOwner) { binding.tvOutstandingValue.text = it.asRupee() }
        viewModel.advanceBalance.observe(viewLifecycleOwner) {
            binding.tvAdvanceBalanceValue.text = it.asRupee()
            binding.groupAdvance.isVisible = it > 0.0
        }
        viewModel.todaySales.observe(viewLifecycleOwner) { binding.tvTodaySalesValue.text = it.asRupee() }
        viewModel.todayPayments.observe(viewLifecycleOwner) { binding.tvTodayPaymentsValue.text = it.asRupee() }
        viewModel.recentTransactions.observe(viewLifecycleOwner) {
            txAdapter.submitList(it)
            binding.progressBar.isVisible = false
            binding.tvEmptyTransactions.isVisible = it.isEmpty()
        }
        viewModel.dailyInsight().observe(viewLifecycleOwner) { binding.tvInsight.text = it }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
