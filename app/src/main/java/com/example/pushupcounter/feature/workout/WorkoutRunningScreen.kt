package com.example.pushupcounter.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.pushupcounter.MainViewModel
import com.example.pushupcounter.WorkoutConfig
import com.example.pushupcounter.WorkoutResult
import kotlinx.coroutines.delay

/**
 * Orchestrates the workout flow:
 * 1. Pre-start "ReadyView" while user adjusts position
 * 2. Live "RunningView" with camera background + counter
 * 3. Auto-finish for challenge mode at 100, or manual finish
 *
 * Integrates with the existing [MainViewModel] camera + counting pipeline.
 */
@Composable
fun WorkoutRunningScreen(
    config: WorkoutConfig,
    vm: MainViewModel,
    onFinish: (WorkoutResult) -> Unit
) {
    var isStarted by remember { mutableStateOf(false) }
    var currentSet by remember { mutableStateOf(1) }
    var setHistory by remember { mutableStateOf<List<Int>>(emptyList()) }
    var elapsedSec by remember { mutableStateOf(0) }
    var inRest by remember { mutableStateOf(false) }
    var restRemaining by remember { mutableStateOf(0) }

    val repCount by vm.repCount

    // Reset on entry; ensure no stale state from a prior session
    LaunchedEffect(Unit) { vm.reset() }
    DisposableEffect(Unit) { onDispose { vm.reset() } }

    // Workout timer (1Hz tick once started)
    LaunchedEffect(isStarted) {
        if (isStarted) {
            while (true) {
                delay(1000L)
                elapsedSec++
            }
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

    // Auto-finish for challenge mode at 100
    LaunchedEffect(repCount) {
        if (config.mode == "challenge" && repCount >= 100 && isStarted) {
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080E18))) {
        if (isStarted) {
            CameraBackground(vm = vm)
            Box(modifier = Modifier.fillMaxSize().background(Color(0xCC000000)))
        }

        if (!isStarted) {
            ReadyView(
                config = config,
                onStart = {
                    isStarted = true
                    vm.startCalibration()
                }
            )
        } else {
            RunningView(
                config = config,
                repCount = repCount,
                currentSet = currentSet,
                setHistory = setHistory,
                inRest = inRest,
                restRemaining = restRemaining,
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
}
