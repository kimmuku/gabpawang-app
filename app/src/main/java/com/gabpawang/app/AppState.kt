package com.gabpawang.app

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringArrayResource

/** Result of a finished workout session. */
data class WorkoutResult(
    val sets: Int,
    val total: Int,
    val history: List<Int>,
    val durationSec: Int = 0
)

/** Configuration for the about-to-start workout. */
data class WorkoutConfig(
    val mode: String, // "free", "target", "challenge", "timed", "udt"
    val targetCounts: List<Int> = listOf(30, 25, 20),
    val targetSets: Int = 3,
    val timedSecs: Int = 120,
    val udtTarget: Int = 30
)

/** Stage thresholds — index 0 corresponds to stage 1 boundary. */
val STAGE_BOUNDARIES = intArrayOf(0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000, 200000)

/** Returns localized stage names (indexed from 1; index 0 is empty). */
@Composable
fun stageNames(): Array<String> = stringArrayResource(R.array.stage_names)

/** Returns localized stage subtitles (indexed from 1; index 0 is empty). */
@Composable
fun stageSubtitles(): Array<String> = stringArrayResource(R.array.stage_subtitles)

/** Returns the localized name for a stage in Composable scope. */
@Composable
fun stageName(stage: Int): String {
    val names = stageNames()
    return names.getOrNull(stage.coerceIn(0, names.size - 1)) ?: ""
}

/** Returns the localized subtitle for a stage in Composable scope. */
@Composable
fun stageSubtitle(stage: Int): String {
    val subs = stageSubtitles()
    return subs.getOrNull(stage.coerceIn(0, subs.size - 1)) ?: ""
}

/** Non-composable version: resolve stage name from a Context. */
fun stageName(context: Context, stage: Int): String {
    val names = context.resources.getStringArray(R.array.stage_names)
    return names.getOrNull(stage.coerceIn(0, names.size - 1)) ?: ""
}

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
 * Returns estimated national top-% text based on max single-set reps (Korean adult male reference).
 * Returns null when no record exists yet.
 */
fun nationalRankText(context: Context, maxReps: Int): String? {
    if (maxReps <= 0) return null
    val resId = when {
        maxReps >= 100 -> R.string.rank_top_0_2
        maxReps >= 90  -> R.string.rank_top_0_3
        maxReps >= 80  -> R.string.rank_top_0_5
        maxReps >= 75  -> R.string.rank_top_0_8
        maxReps >= 70  -> R.string.rank_top_1
        maxReps >= 65  -> R.string.rank_top_1_5
        maxReps >= 60  -> R.string.rank_top_2_5
        maxReps >= 55  -> R.string.rank_top_4_5
        maxReps >= 50  -> R.string.rank_top_7
        maxReps >= 45  -> R.string.rank_top_10
        maxReps >= 40  -> R.string.rank_top_14
        maxReps >= 35  -> R.string.rank_top_22
        maxReps >= 30  -> R.string.rank_top_32
        maxReps >= 25  -> R.string.rank_top_45
        maxReps >= 20  -> R.string.rank_top_57
        maxReps >= 15  -> R.string.rank_top_70
        maxReps >= 10  -> R.string.rank_top_85
        maxReps >= 5   -> R.string.rank_top_92
        else           -> R.string.rank_bottom
    }
    return context.getString(resId)
}

/**
 * Centralized navigation + transient UI state.
 * DB-backed values (charStage, totalPushups, streak) are owned by AppViewModel.
 * Screens: tutorial, home, workoutStart, workout,
 * result, levelup, record, character, challenge, notifications, settings.
 *
 * @param initialScreen the first screen to show; defaults to "home" but can be "tutorial"
 *   for first-time users detected via SharedPreferences.
 */
class AppState(initialScreen: String = "home") {
    private val backStack = mutableStateListOf(initialScreen)

    val screen: String get() = backStack.last()
    val canGoBack: Boolean get() = backStack.size > 1

    var workoutResult by mutableStateOf<WorkoutResult?>(null)
    var workoutConfig by mutableStateOf(WorkoutConfig("free"))
    var levelUpStage by mutableStateOf(1)
    var voiceEnabled by mutableStateOf(true)
    var musicEnabled by mutableStateOf(false)
    var isDarkTheme by mutableStateOf(true)

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
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
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
