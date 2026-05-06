package com.gabpawang.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Data access object for workout session records. */
@Dao
interface WorkoutDao {
    @Insert
    suspend fun insert(session: WorkoutSessionEntity)

    /** Returns all sessions ordered by most recent first. */
    @Query("SELECT * FROM workout_sessions ORDER BY dateMillis DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    /** Returns the accumulated total push-up count across all sessions. */
    @Query("SELECT COALESCE(SUM(totalReps), 0) FROM workout_sessions")
    fun getTotalReps(): Flow<Int>

    /**
     * Returns distinct workout days as 'YYYY-MM-DD' strings in descending order.
     * Used to compute the consecutive-day streak.
     */
    @Query(
        "SELECT DISTINCT strftime('%Y-%m-%d', dateMillis / 1000, 'unixepoch', 'localtime') " +
            "as day FROM workout_sessions ORDER BY day DESC"
    )
    fun getDistinctDays(): Flow<List<String>>
}
