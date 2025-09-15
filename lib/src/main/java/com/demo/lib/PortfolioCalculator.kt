package com.demo.lib

import java.text.NumberFormat
import java.util.Locale

class PortfolioCalculator {

    fun currentValue(holdings: List<Holding>): Double =
        holdings.sumOf { it.ltp * it.quantity }

    fun totalInvestment(holdings: List<Holding>): Double =
        holdings.sumOf { it.avgPrice * it.quantity }

    fun totalPNL(holdings: List<Holding>): Double =
        currentValue(holdings) - totalInvestment(holdings)

    fun todaysPNL(holdings: List<Holding>): Double =
        holdings.sumOf { (it.close - it.ltp) * it.quantity }

    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun calculateSummary(holdings: List<Holding>): PortfolioSummary {
        val currentValue = currentValue(holdings)
        val totalInvestment = totalInvestment(holdings)
        val totalPnl = totalPNL(holdings)
        val todayPnl = todaysPNL(holdings)

        val percentChange = if (totalInvestment != 0.0) {
            (totalPnl / totalInvestment) * 100
        } else 0.0

        return PortfolioSummary(
            currentValue = currencyFormatter.format(currentValue),
            totalInvestment = currencyFormatter.format(totalInvestment),
            todayPnl = currencyFormatter.format(todayPnl),
            totalPnl = String.format(
                "%s (%.2f%%)", currencyFormatter.format(totalPnl), percentChange
            )
        )
    }
}