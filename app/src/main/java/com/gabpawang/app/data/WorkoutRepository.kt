package com.gabpawang.app.data

import com.gabpawang.app.data.db.GabpaDatabase
import com.gabpawang.app.data.db.WorkoutSessionEntity
import com.gabpawang.app.data.remote.SupabaseSync
import com.gabpawang.app.data.remote.dto.WorkoutSessionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** Repository that wraps WorkoutDao and exposes business-level flows. */
class WorkoutRepository(db: GabpaDatabase) {
    private val dao = db.workoutDao()

    val totalReps: Flow<Int> = dao.getTotalReps()
    val allSessions: Flow<List<WorkoutSessionEntity>> = dao.getAllSessions()

    /** Persists a completed session to Room and attempts cloud backup via Supabase. */
    suspend fun saveSession(totalReps: Int, sets: Int, durationSec: Int, history: List<Int> = emptyList(), mode: String = "") {
        val dateMillis = System.currentTimeMillis()
        dao.insert(
            WorkoutSessionEntity(
                dateMillis = dateMillis,
                totalReps = totalReps,
                sets = sets,
                durationSec = durationSec,
                setHistory = history.joinToString(","),
                mode = mode
            )
        )
        SupabaseSync.uploadSession(
            WorkoutSessionDto(
                dateMillis = dateMillis,
                totalReps = totalReps,
                sets = sets,
                durationSec = durationSec
            )
        )
    }

    /**
     * Returns a Flow of the current consecutive-day workout streak.
     * Counts backward from today (or yesterday if today has no session).
     */
    fun streakFlow(): Flow<Int> = dao.getDistinctDays().map { days ->
        if (days.isEmpty()) return@map 0
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        var streak = 0
        for (i in days.indices) {
            val expected = fmt.format(cal.time)
            when {
                days[i] == expected -> {
                    streak++
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                }
                i == 0 -> {
                    // Today has no session — try starting from yesterday
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    if (days[0] == fmt.format(cal.time)) {
                        streak++
                        cal.add(Calendar.DAY_OF_YEAR, -1)
                    } else break
                }
                else -> break
            }
        }
        streak
    }
}
