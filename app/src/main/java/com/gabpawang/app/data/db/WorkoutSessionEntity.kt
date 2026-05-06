package com.gabpawang.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents a single completed workout session stored in the local Room database. */
@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,   // System.currentTimeMillis() at session end
    val totalReps: Int,     // total push-ups in this session
    val sets: Int,          // number of sets
    val durationSec: Int    // workout duration in seconds
)
