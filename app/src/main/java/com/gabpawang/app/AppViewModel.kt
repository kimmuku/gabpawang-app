package com.gabpawang.app

import android.app.Application
import android.content.Context
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
    private val authRepo get() = (getApplication<GabpaApplication>()).authRepository

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
        sessions.maxOfOrNull { s -> if (s.sets > 0) s.totalReps / s.sets else s.totalReps } ?: 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Persists a finished workout result to the database. */
    fun saveWorkout(result: WorkoutResult) {
        viewModelScope.launch {
            repo.saveSession(
                totalReps = result.total,
                sets = result.sets,
                durationSec = result.durationSec
            )
        }
    }

    /** Starts Google sign-in flow via CredentialManager. Requires Activity context. */
    fun signInWithGoogle(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            authRepo.signInWithGoogle()
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Google 로그인 실패") }
        }
    }

    /** Starts Kakao sign-in flow via Kakao SDK. Requires Activity context. */
    fun signInWithKakao(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            authRepo.signInWithKakao()
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "카카오 로그인 실패") }
        }
    }
}
