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
                when {
                    state.isLoading -> {
                        showLoadingState()
                    }
                    state.error != null -> {
                        showErrorState(state.error)
                    }
                    else -> {
                        showSuccessState(state.data)
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

    private fun showLoadingState() {
        binding.mainContent.visibility = View.GONE
        binding.loadingLayout.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showErrorState(errorMessage: String?) {
        binding.mainContent.visibility = View.GONE
        binding.loadingLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        
        binding.errorMessage.text = errorMessage ?: getString(R.string.error_loading_holdings)
        
        binding.retryButton.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun showSuccessState(data: List<com.demo.assignmentapplication.data.local.HoldingEntity>) {
        binding.mainContent.visibility = View.VISIBLE
        binding.loadingLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
        
        adapter.submitList(data)

        if (data.isNotEmpty()) {
            val summary = calculator.calculateSummary(data.map { it.toHolding() })

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}