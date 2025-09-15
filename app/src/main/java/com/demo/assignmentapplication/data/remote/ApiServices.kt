package com.demo.assignmentapplication.data.remote

import com.demo.assignmentapplication.domain.model.HoldingsResponse
import retrofit2.http.GET

interface ApiServices {

    @GET("/")
    suspend fun getHoldings(): HoldingsResponse
}