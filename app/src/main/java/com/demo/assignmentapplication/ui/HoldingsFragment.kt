package com.demo.assignmentapplication.ui

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.assignmentapplication.R
import com.demo.assignmentapplication.databinding.FragmentHoldingsBinding
import com.demo.assignmentapplication.ui.adapter.HoldingsAdapter
import com.demo.assignmentapplication.ui.component.SummaryCard
import com.demo.assignmentapplication.ui.viewmodel.HoldingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HoldingsFragment : Fragment(R.layout.fragment_holdings) {

    private val viewModel: HoldingsViewModel by viewModels()
    private lateinit var adapter: HoldingsAdapter

    private var _binding: FragmentHoldingsBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHoldingsBinding.bind(view)

        adapter = HoldingsAdapter()
        binding?.holdingsRecyclerView?.adapter = adapter
        binding?.holdingsRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                adapter.submitList(state.data)

                binding?.summaryCard?.apply {
                    setViewCompositionStrategy(
                        ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                    )
                    setContent {
                        val state by viewModel.state.collectAsStateWithLifecycle()
                        SummaryCard(state.data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}