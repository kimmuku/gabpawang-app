package com.gabpawang.app.feature.workout

import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
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
import androidx.compose.ui.res.stringResource
import com.gabpawang.app.R
import com.gabpawang.app.MainViewModel
import com.gabpawang.app.ads.InterstitialAdManager
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
private fun currentClock(formatter: DateTimeFormatter): String =
    LocalDateTime.now().format(formatter)

@Composable
fun WorkoutRunningScreen(
    config: WorkoutConfig,
    vm: MainViewModel,
    voiceEnabled: Boolean = false,
    musicEnabled: Boolean = false,
    totalPushups: Int = 0,
    onFinish: (WorkoutResult) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val clockPattern = stringResource(R.string.workout_clock_format)
    val clockFormatter = remember(clockPattern) {
        DateTimeFormatter.ofPattern(clockPattern, Locale.getDefault())
    }
    var currentSet by remember { mutableStateOf(1) }
    var setHistory by remember { mutableStateOf<List<Int>>(emptyList()) }
    var elapsedSec by remember { mutableStateOf(0) }
    var inRest by remember { mutableStateOf(false) }
    var restRemaining by remember { mutableStateOf(0) }
    var restTotalSec by remember { mutableStateOf(60) }
    var countdownRemaining by remember { mutableStateOf(10) }
    var clockText by remember { mutableStateOf(currentClock(clockFormatter)) }
    val nextLevelGoal = remember(totalPushups) { nextThresholdFor(stageFor(totalPushups)) }
    var udtPhase by remember { mutableStateOf("down") }

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

    // Background music — starts after countdown, pauses during rest, resumes on next set
    val bgmPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    var bgmStarted by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        if (musicEnabled) {
            val trackId = listOf(
                R.raw.workout_bgm_1,
                R.raw.workout_bgm_2,
                R.raw.workout_bgm_3
            ).random()
            val mp = MediaPlayer.create(context, trackId)
            mp?.isLooping = true
            mp?.setVolume(0.5f, 0.5f)
            bgmPlayer.value = mp
        }
        onDispose {
            bgmPlayer.value?.stop()
            bgmPlayer.value?.release()
            bgmPlayer.value = null
        }
    }
    LaunchedEffect(Unit) {
        while (countdownRemaining > 0) delay(200L)
        if (musicEnabled) {
            bgmPlayer.value?.start()
            bgmStarted = true
        }
    }
    LaunchedEffect(inRest) {
        if (!musicEnabled || !bgmStarted) return@LaunchedEffect
        if (inRest) bgmPlayer.value?.pause()
        else bgmPlayer.value?.start()
    }

    // Reset on entry; calibration defers until the pre-start countdown finishes for all modes.
    LaunchedEffect(Unit) {
        vm.reset()
    }
    DisposableEffect(Unit) { onDispose { vm.reset() } }

    // Pre-start countdown for all modes; all modes start pose calibration after.
    LaunchedEffect(Unit) {
        while (countdownRemaining > 0) {
            delay(1000L)
            countdownRemaining--
        }
        vm.startCalibration()
    }

    // UDT paced beep cycle: every 3s play DOWN tone then UP tone. Count comes from pose detection.
    val toneGen = remember(config.mode) {
        if (config.mode == "udt") ToneGenerator(AudioManager.STREAM_MUSIC, 80) else null
    }
    DisposableEffect(toneGen) { onDispose { toneGen?.release() } }
    val upLabel = stringResource(R.string.phase_up)
    val downLabel = stringResource(R.string.phase_down)
    LaunchedEffect(config.mode) {
        if (config.mode != "udt") return@LaunchedEffect
        while (countdownRemaining > 0) delay(200L)
        // Wait until pose detection is calibrated and ready to count.
        while (phase != upLabel && phase != downLabel) delay(200L)
        while (true) {
            udtPhase = "down"
            toneGen?.startTone(ToneGenerator.TONE_CDMA_LOW_L, 200)
            delay(1500L)
            udtPhase = "up"
            toneGen?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 200)
            delay(1500L)
        }
    }

    // Auto-finish for UDT when target reached (count comes from pose detection)
    LaunchedEffect(repCount) {
        if (config.mode == "udt" && repCount >= config.udtTarget) {
            onFinish(
                WorkoutResult(
                    sets = 1,
                    total = repCount,
                    history = listOf(repCount),
                    durationSec = elapsedSec
                )
            )
        }
    }

    // Clock and workout timer (1Hz tick each)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            clockText = currentClock(clockFormatter)
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
                    restTotalSec = 60
                    inRest = true
                    vm.reset()
                }
                activity?.let { InterstitialAdManager.onSetCompleted(it) }
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
            restTotalSec = restTotalSec,
            phase = phase,
            elapsedSec = elapsedSec,
            countdownRemaining = countdownRemaining,
            clockText = clockText,
            displayTotal = totalPushups + setHistory.sum() + repCount,
            nextLevelGoal = nextLevelGoal,
            udtCount = repCount,
            udtPhase = udtPhase,
            onExtendRest = {
                restRemaining += 30
                restTotalSec += 30
            },
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
                    restTotalSec = 60
                    inRest = true
                    vm.reset()
                }
                activity?.let { InterstitialAdManager.onSetCompleted(it) }
            },
            onFinishAll = {
                val finalHistory = when (config.mode) {
                    "udt" -> if (repCount > 0) listOf(repCount) else emptyList()
                    else -> if (repCount > 0) setHistory + repCount else setHistory
                }
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
