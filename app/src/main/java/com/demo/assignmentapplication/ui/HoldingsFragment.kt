package com.demo.assignmentapplication.ui

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.assignmentapplication.R
import com.demo.assignmentapplication.databinding.FragmentHoldingsBinding
import com.demo.assignmentapplication.ui.adapter.HoldingsAdapter
import com.demo.assignmentapplication.ui.viewmodel.HoldingsViewModel
import com.demo.assignmentapplication.util.AnimationUtil.collapseView
import com.demo.assignmentapplication.util.AnimationUtil.expandView
import com.demo.assignmentapplication.util.toHolding
import com.demo.lib.PortfolioCalculator
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject

@AndroidEntryPoint
class HoldingsFragment : Fragment(R.layout.fragment_holdings) {

    @Inject
    lateinit var calculator: PortfolioCalculator

    private val viewModel: HoldingsViewModel by viewModels()
    private lateinit var adapter: HoldingsAdapter

    private var _binding: FragmentHoldingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHoldingsBinding.bind(view)


        adapter = HoldingsAdapter()
        binding.holdingsRecyclerView.adapter = adapter
        binding.holdingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                val list = state.data
                adapter.submitList(list)

                if (!list.isNullOrEmpty()) {
                    // use lib function to calculate formatted summary
                    val summary = calculator.calculateSummary(list.map { it.toHolding() })

                    // Update UI
                    binding.currentValue.text = summary.currentValue
                    binding.totalInvestment.text = summary.totalInvestment

                    binding.todayPnl.apply {
                        text = summary.todayPnl
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (summary.todayPnl.startsWith("-")) R.color.loss_red else R.color.profit_green
                            )
                        )
                    }

                    binding.totalPnl.apply {
                        text = summary.totalPnl
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (summary.totalPnl.startsWith("-")) R.color.loss_red else R.color.profit_green
                            )
                        )
                    }
                }
            }
        }


        var isExpanded = false
        binding.headerRow.setOnClickListener {
            isExpanded = !isExpanded

            if (isExpanded) {
                expandView(binding.expandableLayout)
                binding.arrowIcon.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
            } else {
                collapseView(binding.expandableLayout)
                binding.arrowIcon.setImageResource(R.drawable.outline_arrow_drop_down_24)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}