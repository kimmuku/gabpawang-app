package com.gabpawang.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Result of a finished workout session. */
data class WorkoutResult(
    val sets: Int,
    val total: Int,
    val history: List<Int>,
    val durationSec: Int = 0
)

/** Configuration for the about-to-start workout. */
data class WorkoutConfig(
    val mode: String, // "free", "target", "challenge"
    val targetCounts: List<Int> = listOf(30, 25, 20),
    val targetSets: Int = 3
)

/** Stage thresholds — index 0 corresponds to stage 1 boundary. */
val STAGE_BOUNDARIES = intArrayOf(0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000)

/** Stage names indexed from 1 (index 0 is unused). */
val STAGE_NAMES = arrayOf(
    "",
    "마른 체형",
    "약간 다듬어진",
    "보통 운동인",
    "단단한 체형",
    "근육질",
    "대흉근 부각",
    "갑빠 형태 완성",
    "보디빌더형",
    "갑빠왕"
)

/** Returns the current stage (1..9) based on accumulated push-ups. */
fun stageFor(total: Int): Int {
    var stage = 1
    for ((idx, threshold) in STAGE_BOUNDARIES.withIndex()) {
        if (total >= threshold) stage = idx + 1
    }
    return stage.coerceIn(1, 9)
}

/** Returns the threshold for a given stage (1..9). */
fun thresholdFor(stage: Int): Int {
    val s = stage.coerceIn(1, 9)
    return STAGE_BOUNDARIES[s - 1]
}

/** Returns the threshold of the next stage, or current threshold if already maxed. */
fun nextThresholdFor(stage: Int): Int {
    val s = stage.coerceIn(1, 9)
    return if (s >= 9) STAGE_BOUNDARIES[8] else STAGE_BOUNDARIES[s]
}

/**
 * Centralized navigation + transient UI state.
 * DB-backed values (charStage, totalPushups, streak) are owned by AppViewModel.
 * Screens: splash, onboarding, signup, home, workoutStart, workout,
 * result, levelup, record, character, challenge, notifications, settings.
 */
class AppState {
    private val backStack = mutableStateListOf("splash")

    val screen: String get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    var workoutResult by mutableStateOf<WorkoutResult?>(null)
    var workoutConfig by mutableStateOf(WorkoutConfig("free"))
    var recordInitialTab by mutableStateOf("1rm")
    var levelUpStage by mutableStateOf(1)

    /** Push a new screen onto the back stack. */
    fun go(s: String) {
        backStack.add(s)
    }

    /** Clear the stack and set a single root screen (no back from here). */
    fun goRoot(s: String) {
        backStack.clear()
        backStack.add(s)
    }

    /** Pop the current screen; does nothing if already at root. */
    fun back() {
        if (backStack.size > 1) backStack.removeLast()
    }

    /** Bottom-nav routes always reset the stack so back exits the app from any tab. */
    fun goNav(id: String) {
        when (id) {
            "home" -> goRoot("home")
            "record" -> goRoot("record")
            "challenge" -> goRoot("challenge")
            "settings" -> goRoot("settings")
        }
    }

    /** Called when a workout session completes. Stack is cleared so back cannot re-enter the camera. */
    fun onWorkoutFinish(result: WorkoutResult) {
        workoutResult = result
        goRoot("result")
    }
}
