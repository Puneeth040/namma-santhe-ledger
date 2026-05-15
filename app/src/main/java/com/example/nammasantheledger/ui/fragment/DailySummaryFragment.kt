package com.example.nammasantheledger.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nammasantheledger.databinding.FragmentDailySummaryBinding
import com.example.nammasantheledger.ui.AppGraph
import com.example.nammasantheledger.ui.LedgerViewModel
import com.example.nammasantheledger.ui.LedgerViewModelFactory
import com.example.nammasantheledger.ui.util.asRupee

class DailySummaryFragment : Fragment() {
    private var _binding: FragmentDailySummaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LedgerViewModel by activityViewModels {
        LedgerViewModelFactory(AppGraph.repository(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDailySummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var sales = 0.0
        var payments = 0.0
        var outstanding = 0.0
        binding.progressBar.isVisible = true
        fun render() {
            binding.tvSalesValue.text = sales.asRupee()
            binding.tvCreditValue.text = sales.asRupee()
            binding.tvPaymentsValue.text = payments.asRupee()
            val safeOutstanding = outstanding.coerceAtLeast(0.0)
            val advance = (outstanding * -1).coerceAtLeast(0.0)
            binding.tvOutstandingValue.text = safeOutstanding.asRupee()
            binding.tvAdvanceValue.text = advance.asRupee()
            binding.tvAdvanceLabel.isVisible = advance > 0.0
            binding.tvAdvanceValue.isVisible = advance > 0.0
            binding.tvProfitEstimateValue.text = (payments - (sales - payments)).asRupee()
            binding.progressBar.isVisible = false
        }
        viewModel.todaySales.observe(viewLifecycleOwner) { sales = it ?: 0.0; render() }
        viewModel.todayPayments.observe(viewLifecycleOwner) { payments = it ?: 0.0; render() }
        viewModel.totalOutstanding.observe(viewLifecycleOwner) { outstanding = it ?: 0.0; render() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
