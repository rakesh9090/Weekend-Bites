package com.demo.assignmentapplication.data.repository

import com.demo.assignmentapplication.data.local.SampleDao
import com.demo.assignmentapplication.data.local.SampleEntity
import com.demo.assignmentapplication.data.remote.ApiServices
import com.demo.assignmentapplication.data.remote.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SampleRepositoryImpl @Inject constructor(
    private val api: ApiServices,
    private val dao: SampleDao
) : SampleRepository {
    override fun getSamples(): Flow<Resource<List<SampleEntity>>> = flow {
        emit(Resource.loading())
        try {
            dao.getAll().collect { list ->
                emit(Resource.success(list))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "DB error"))
        }
    }


    override suspend fun refreshSamples() {
        try {
            val response = api.getHomepage()
            val mapped = response.mapIndexed { index, json ->
                SampleEntity(
                    id = index,
                    name = json["name"]?.asString ?: "Unknown",
                    age = json["age"]?.asInt ?: 0
                )
            }
            dao.insertAll(mapped)
        } catch (e: Exception) {
            // Log or handle error
        }
    }

}