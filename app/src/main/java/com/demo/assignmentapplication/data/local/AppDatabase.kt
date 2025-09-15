package com.demo.assignmentapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HoldingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun holdingDao(): HoldingDao
}