package com.demo.assignmentapplication.util

import com.demo.assignmentapplication.data.local.HoldingEntity
import com.demo.lib.Holding

fun HoldingEntity.toHolding(): Holding =
    Holding(symbol, quantity, ltp, avgPrice, close)