package com.demo.assignmentapplication.domain.model

import com.demo.assignmentapplication.data.local.SampleEntity

data class SampleState(
    val isLoading: Boolean = false,
    val data: List<SampleEntity> = emptyList(),
    val error: String? = null
)
