package com.gabpawang.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gabpawang.app.data.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.gabpawang.app.data.db.WorkoutSessionEntity

/**
 * ViewModel exposing DB-backed state (totalPushups, charStage, streak) and
 * social sign-in actions (Google, Kakao) as Compose-friendly StateFlows.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: WorkoutRepository = (application as GabpaApplication).repository
    val totalPushups: StateFlow<Int> = repo.totalReps.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val charStage: StateFlow<Int> = totalPushups.map { stageFor(it) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 1
    )

    val streak: StateFlow<Int> = repo.streakFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    val oneRepMax: StateFlow<Int> = repo.allSessions.map { sessions ->
        sessions.flatMap { s ->
            s.setHistory.split(",").mapNotNull { it.trim().toIntOrNull() }
                .ifEmpty { listOf(s.totalReps) }
        }.maxOrNull() ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Persists a finished workout result to the database. */
    fun saveWorkout(result: WorkoutResult, mode: String = "") {
        viewModelScope.launch {
            repo.saveSession(
                totalReps = result.total,
                sets = result.sets,
                durationSec = result.durationSec,
                history = result.history,
                mode = mode
            )
        }
    }

}
