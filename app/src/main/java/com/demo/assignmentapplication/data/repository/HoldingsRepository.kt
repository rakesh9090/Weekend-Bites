package com.demo.assignmentapplication.data.repository

import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.assignmentapplication.data.remote.Resource
import kotlinx.coroutines.flow.Flow

interface HoldingsRepository {
    fun getHoldings(): Flow<Resource<List<HoldingEntity>>>
    suspend fun refreshHoldings()
}
