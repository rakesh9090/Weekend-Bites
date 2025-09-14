package com.demo.assignmentapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.assignmentapplication.data.remote.Status
import com.demo.assignmentapplication.data.repository.SampleRepository
import com.demo.assignmentapplication.domain.model.SampleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class SampleViewModel @Inject constructor(private val repository: SampleRepository) : ViewModel() {
    val state: StateFlow<SampleState> = repository.getSamples()
        .map { resource ->
            when (resource.status) {
                Status.LOADING -> SampleState(isLoading = true)
                Status.SUCCESS -> SampleState(data = resource.data ?: emptyList())
                Status.ERROR -> SampleState(error = resource.message)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SampleState()
        )

    fun refresh() {
        viewModelScope.launch {
            repository.refreshSamples()
        }
    }


}