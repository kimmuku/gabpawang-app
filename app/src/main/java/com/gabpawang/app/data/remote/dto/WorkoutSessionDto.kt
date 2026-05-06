package com.gabpawang.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTO for uploading a workout session to Supabase workout_sessions table. */
@Serializable
data class WorkoutSessionDto(
    @SerialName("date_millis") val dateMillis: Long,
    @SerialName("total_reps") val totalReps: Int,
    val sets: Int,
    @SerialName("duration_sec") val durationSec: Int
)
