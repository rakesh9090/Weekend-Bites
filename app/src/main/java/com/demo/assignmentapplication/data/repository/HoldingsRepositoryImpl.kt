package com.demo.assignmentapplication.data.repository

import com.demo.assignmentapplication.data.local.HoldingDao
import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.assignmentapplication.data.remote.ApiServices
import com.demo.assignmentapplication.data.remote.Resource
import com.demo.assignmentapplication.util.Constant.DB_ERROR
import com.demo.assignmentapplication.util.Constant.NO_DATA
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class HoldingsRepositoryImpl @Inject constructor(
    private val api: ApiServices,
    private val dao: HoldingDao
) : HoldingsRepository {

    private var hasSuccessfullyFetchedData = false

    override fun getHoldings(): Flow<Resource<List<HoldingEntity>>> =
        dao.getAll()
            .map { list -> 
                when {
                    list.isNotEmpty() -> Resource.success(list)
                    hasSuccessfullyFetchedData -> Resource.success(list) // Empty but previously fetched
                    else -> Resource.error(NO_DATA)
                }
            }
            .catch { e -> emit(Resource.error(e.message ?: DB_ERROR)) }
            .onStart { emit(Resource.loading()) }


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
            hasSuccessfullyFetchedData = true
        } catch (e: Exception) {
        }
    }
}
