package com.demo.assignmentapplication.domain.model

import com.demo.assignmentapplication.data.local.HoldingEntity

data class HoldingsState(
    val isLoading: Boolean = false,
    val data: List<HoldingEntity> = emptyList(),
    val error: String? = null
)
