package com.gabpawang.app.feature.workout

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gabpawang.app.MainViewModel
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.WorkoutResult
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.stageFor
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Orchestrates the workout flow:
 * 1. Live "RunningView" with camera background + counter (starts immediately)
 * 2. Auto-finish for challenge mode at 100, or manual finish
 *
 * Integrates with the existing [MainViewModel] camera + counting pipeline.
 */
private val clockFormatter = DateTimeFormatter.ofPattern("M월 d일 (E) HH:mm:ss", Locale.KOREAN)
private fun currentClock(): String = LocalDateTime.now().format(clockFormatter)

@Composable
fun WorkoutRunningScreen(
    config: WorkoutConfig,
    vm: MainViewModel,
    voiceEnabled: Boolean = false,
    totalPushups: Int = 0,
    onFinish: (WorkoutResult) -> Unit
) {
    val context = LocalContext.current
    var currentSet by remember { mutableStateOf(1) }
    var setHistory by remember { mutableStateOf<List<Int>>(emptyList()) }
    var elapsedSec by remember { mutableStateOf(0) }
    var inRest by remember { mutableStateOf(false) }
    var restRemaining by remember { mutableStateOf(0) }
    var countdownRemaining by remember { mutableStateOf(if (config.mode == "timed") 10 else 0) }
    var clockText by remember { mutableStateOf(currentClock()) }
    val nextLevelGoal = remember(totalPushups) { nextThresholdFor(stageFor(totalPushups)) }

    val repCount by vm.repCount
    val phase by vm.phase

    // TTS for voice count
    val ttsRef = remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        var callbackFired = false
        tts = TextToSpeech(context) { status ->
            callbackFired = true
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(Locale.KOREA)
                ttsRef.value = tts
            }
        }
        // If onInit fired synchronously (tts was null inside callback), wire it up now
        if (callbackFired && ttsRef.value == null) {
            tts?.setLanguage(Locale.KOREA)
            ttsRef.value = tts
        }
        onDispose {
            tts?.stop()
            tts?.shutdown()
            ttsRef.value = null
        }
    }

    // Reset on entry; timed mode defers calibration until countdown finishes
    LaunchedEffect(Unit) {
        vm.reset()
        if (config.mode != "timed") vm.startCalibration()
    }
    DisposableEffect(Unit) { onDispose { vm.reset() } }

    // Pre-start countdown for timed mode, then kick off calibration
    LaunchedEffect(Unit) {
        while (countdownRemaining > 0) {
            delay(1000L)
            countdownRemaining--
        }
        if (config.mode == "timed") vm.startCalibration()
    }

    // Clock and workout timer (1Hz tick each)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            clockText = currentClock()
        }
    }
    LaunchedEffect(Unit) {
        while (countdownRemaining > 0) delay(200L)
        while (true) {
            delay(1000L)
            elapsedSec++
        }
    }

    // Rest countdown — when finished, kick off the next set's calibration
    LaunchedEffect(inRest) {
        if (inRest) {
            while (restRemaining > 0) {
                delay(1000L)
                restRemaining--
            }
            inRest = false
            vm.startCalibration()
        }
    }

    // Speak count on each rep if voice is enabled — mirrors the on-screen display value
    LaunchedEffect(repCount) {
        if (voiceEnabled && repCount > 0) {
            val spoken = when (config.mode) {
                "target" -> {
                    val target = config.targetCounts.getOrElse(currentSet - 1) { 20 }
                    (target - repCount).coerceAtLeast(0)
                }
                "challenge" -> (100 - repCount).coerceAtLeast(0)
                else -> repCount
            }
            ttsRef.value?.speak("$spoken", TextToSpeech.QUEUE_FLUSH, null, "rep_$repCount")
        }
    }

    // Auto-finish for timed mode at 120 seconds
    LaunchedEffect(elapsedSec) {
        if (config.mode == "timed" && elapsedSec >= config.timedSecs) {
            val finalHistory = if (repCount > 0) listOf(repCount) else emptyList()
            onFinish(
                WorkoutResult(
                    sets = finalHistory.size,
                    total = finalHistory.sum(),
                    history = finalHistory,
                    durationSec = elapsedSec
                )
            )
        }
    }

    // Auto-finish for challenge mode at 100
    LaunchedEffect(repCount) {
        if (config.mode == "challenge" && repCount >= 100) {
            val finalHistory = setHistory + repCount
            onFinish(
                WorkoutResult(
                    sets = finalHistory.size,
                    total = finalHistory.sum(),
                    history = finalHistory,
                    durationSec = elapsedSec
                )
            )
        }
    }

    // Auto-complete set / workout when target rep count is reached in target mode
    LaunchedEffect(repCount) {
        if (config.mode == "target" && !inRest) {
            val target = config.targetCounts.getOrElse(currentSet - 1) { 20 }
            if (repCount >= target) {
                delay(800L) // brief pause so user sees "0" before transitioning
                val updatedHistory = setHistory + repCount
                if (currentSet >= config.targetSets) {
                    onFinish(
                        WorkoutResult(
                            sets = updatedHistory.size,
                            total = updatedHistory.sum(),
                            history = updatedHistory,
                            durationSec = elapsedSec
                        )
                    )
                } else {
                    setHistory = updatedHistory
                    currentSet++
                    restRemaining = 60
                    inRest = true
                    vm.reset()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080E18))) {
        CameraBackground(vm = vm)
        RunningView(
            config = config,
            repCount = repCount,
            currentSet = currentSet,
            setHistory = setHistory,
            inRest = inRest,
            restRemaining = restRemaining,
            phase = phase,
            elapsedSec = elapsedSec,
            countdownRemaining = countdownRemaining,
            clockText = clockText,
            displayTotal = totalPushups + setHistory.sum() + repCount,
            nextLevelGoal = nextLevelGoal,
            onExtendRest = { restRemaining += 30 },
            onSkipRest = { restRemaining = 0 },
            onCompleteSet = {
                val updatedHistory = setHistory + repCount
                if (config.mode == "target" && currentSet >= config.targetSets) {
                    onFinish(
                        WorkoutResult(
                            sets = updatedHistory.size,
                            total = updatedHistory.sum(),
                            history = updatedHistory,
                            durationSec = elapsedSec
                        )
                    )
                } else {
                    setHistory = updatedHistory
                    currentSet++
                    restRemaining = 60
                    inRest = true
                    vm.reset()
                }
            },
            onFinishAll = {
                val finalHistory = if (repCount > 0) setHistory + repCount else setHistory
                onFinish(
                    WorkoutResult(
                        sets = finalHistory.size,
                        total = finalHistory.sum(),
                        history = finalHistory,
                        durationSec = elapsedSec
                    )
                )
            }
        )
    }
}
