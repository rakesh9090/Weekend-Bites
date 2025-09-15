package com.demo.assignmentapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.assignmentapplication.data.remote.Status
import com.demo.assignmentapplication.data.repository.HoldingsRepository
import com.demo.assignmentapplication.domain.model.HoldingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HoldingsViewModel @Inject constructor(
    private val repository: HoldingsRepository
) : ViewModel() {

    val state: StateFlow<HoldingsState> = repository.getHoldings()
        .map { resource ->
            when (resource.status) {
                Status.LOADING -> HoldingsState(isLoading = true)
                Status.SUCCESS -> HoldingsState(data = resource.data ?: emptyList())
                Status.ERROR -> HoldingsState(error = resource.message)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HoldingsState()
        )

    fun refresh() {
        viewModelScope.launch {
            repository.refreshHoldings()
        }
    }
}