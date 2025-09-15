package com.demo.assignmentapplication.ui.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.lib.PortfolioCalculator
import com.demo.assignmentapplication.util.toDomain

@Composable
fun SummaryCard(holdings: List<HoldingEntity>) {
    val calculator = PortfolioCalculator()
    var expanded by remember { mutableStateOf(false) }

    val currentValue = calculator.currentValue(holdings.map { it.toDomain() })
    val totalInvestment = calculator.totalInvestment(holdings.map { it.toDomain() })
    val totalPnl = calculator.totalPNL(holdings.map { it.toDomain() })
    val todaysPnl = calculator.todaysPNL(holdings.map { it.toDomain() })

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Current Value: ₹${"%.2f".format(currentValue)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface)

            if (expanded) {
                Text("Total Investment: ₹${"%.2f".format(totalInvestment)}",
                    color = MaterialTheme.colorScheme.onSurface)

                Text("Today's P&L: ₹${"%.2f".format(todaysPnl)}",
                    color = if (todaysPnl >= 0) Color(0xFF1BA672) else Color(0xFFD32F2F))

                Text("Total P&L: ₹${"%.2f".format(totalPnl)}",
                    color = if (totalPnl >= 0) Color(0xFF1BA672) else Color(0xFFD32F2F))
            }

        }
    }
}
