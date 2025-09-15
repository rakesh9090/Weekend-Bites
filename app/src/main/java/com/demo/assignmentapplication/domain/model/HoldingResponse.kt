package com.demo.assignmentapplication.domain.model


data class HoldingsResponse(
    val data: HoldingsData
)

data class HoldingsData(
    val userHolding: List<HoldingDto>
)

data class HoldingDto(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)
