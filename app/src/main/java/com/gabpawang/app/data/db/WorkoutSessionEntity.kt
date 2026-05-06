package com.gabpawang.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Represents a single completed workout session stored in the local Room database. */
@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateMillis: Long,
    val totalReps: Int,
    val sets: Int,
    val durationSec: Int,
    val setHistory: String = "",
    val mode: String = ""  // "free", "target", "challenge"
)
