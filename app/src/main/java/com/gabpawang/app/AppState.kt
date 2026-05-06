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
val STAGE_BOUNDARIES = intArrayOf(0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000, 200000)

/** Stage names indexed from 1 (index 0 is unused). */
val STAGE_NAMES = arrayOf(
    "",
    "초보의 시작",
    "의욕 충만",
    "훈련생",
    "탄탄한 기초",
    "갑빠의 각성",
    "강한 전사",
    "엘리트 챔피언",
    "왕의 위엄",
    "전설의 경지",
    "최강! 갑빠왕"
)

/** Stage subtitles indexed from 1 (index 0 is unused). */
val STAGE_SUBTITLES = arrayOf(
    "",
    "작지만 용감한 도전자!",
    "조금씩 강해지는 중!",
    "땀 흘리며 성장 중!",
    "기본이 잡히는 단계!",
    "가슴이 커지기 시작!",
    "싸울 준비 완료!",
    "모두가 인정하는 강자!",
    "누구도 넘볼 수 없는 존재!",
    "한계를 초월한 왕!",
    "푸시업의 신! 전설의 완성!"
)

/** Returns the current stage (1..10) based on accumulated push-ups. */
fun stageFor(total: Int): Int {
    var stage = 1
    for ((idx, threshold) in STAGE_BOUNDARIES.withIndex()) {
        if (total >= threshold) stage = idx + 1
    }
    return stage.coerceIn(1, 10)
}

/** Returns the threshold for a given stage (1..10). */
fun thresholdFor(stage: Int): Int {
    val s = stage.coerceIn(1, 10)
    return STAGE_BOUNDARIES[s - 1]
}

/** Returns the threshold of the next stage, or current threshold if already maxed. */
fun nextThresholdFor(stage: Int): Int {
    val s = stage.coerceIn(1, 10)
    return if (s >= 10) STAGE_BOUNDARIES[9] else STAGE_BOUNDARIES[s]
}

/**
 * Centralized navigation + transient UI state.
 * DB-backed values (charStage, totalPushups, streak) are owned by AppViewModel.
 * Screens: splash, onboarding, signup, home, workoutStart, workout,
 * result, levelup, record, character, challenge, notifications, settings.
 */
class AppState {
    private val backStack = mutableStateListOf("home")

    val screen: String get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    var workoutResult by mutableStateOf<WorkoutResult?>(null)
    var workoutConfig by mutableStateOf(WorkoutConfig("free"))
    var levelUpStage by mutableStateOf(1)
    var voiceEnabled by mutableStateOf(false)

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
            "settings" -> goRoot("settings")
        }
    }

    /** Called when a workout session completes. Stack is cleared so back cannot re-enter the camera. */
    fun onWorkoutFinish(result: WorkoutResult) {
        workoutResult = result
        goRoot("result")
    }
}
