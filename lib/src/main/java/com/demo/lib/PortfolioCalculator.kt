package com.demo.lib

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)

class PortfolioCalculator {

    fun currentValue(holdings: List<Holding>): Double =
        holdings.sumOf { it.ltp * it.quantity }

    fun totalInvestment(holdings: List<Holding>): Double =
        holdings.sumOf { it.avgPrice * it.quantity }

    fun totalPNL(holdings: List<Holding>): Double =
        currentValue(holdings) - totalInvestment(holdings)

    fun todaysPNL(holdings: List<Holding>): Double =
        holdings.sumOf { (it.close - it.ltp) * it.quantity }
}