package com.demo.assignmentapplication.data.repository

import com.demo.assignmentapplication.data.local.SampleEntity
import com.demo.assignmentapplication.data.remote.Resource
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    fun getSamples(): Flow<Resource<List<SampleEntity>>>
    suspend fun refreshSamples()
}