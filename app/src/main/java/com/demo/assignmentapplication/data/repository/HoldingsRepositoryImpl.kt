package com.demo.assignmentapplication.data.repository

import com.demo.assignmentapplication.data.local.HoldingDao
import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.assignmentapplication.data.remote.ApiServices
import com.demo.assignmentapplication.data.remote.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HoldingsRepositoryImpl @Inject constructor(
    private val api: ApiServices,
    private val dao: HoldingDao
) : HoldingsRepository {

    override fun getHoldings(): Flow<Resource<List<HoldingEntity>>> = flow {
        emit(Resource.loading())
        try {
            dao.getAll().collect { list ->
                emit(Resource.success(list))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.message ?: "DB error"))
        }
    }

    override suspend fun refreshHoldings() {
        try {
            val response = api.getHoldings()
            val mapped = response.data.userHolding.map { dto ->
                HoldingEntity(
                    symbol = dto.symbol,
                    quantity = dto.quantity,
                    ltp = dto.ltp,
                    avgPrice = dto.avgPrice,
                    close = dto.close
                )
            }
            dao.insertAll(mapped)
        } catch (e: Exception) {
            // TODO: log or handle gracefully
        }
    }
}
