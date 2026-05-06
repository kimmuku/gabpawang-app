package com.gabpawang.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

/** Room database for the Gabpawang app. Version 1 — single entity. */
@Database(entities = [WorkoutSessionEntity::class], version = 1, exportSchema = false)
abstract class GabpaDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
