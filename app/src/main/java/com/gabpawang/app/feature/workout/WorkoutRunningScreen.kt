package com.gabpawang.app.feature.workout

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
import com.gabpawang.app.MainViewModel
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.WorkoutResult
import kotlinx.coroutines.delay

/**
 * Orchestrates the workout flow:
 * 1. Live "RunningView" with camera background + counter (starts immediately)
 * 2. Auto-finish for challenge mode at 100, or manual finish
 *
 * Integrates with the existing [MainViewModel] camera + counting pipeline.
 */
@Composable
fun WorkoutRunningScreen(
    config: WorkoutConfig,
    vm: MainViewModel,
    onFinish: (WorkoutResult) -> Unit
) {
    var currentSet by remember { mutableStateOf(1) }
    var setHistory by remember { mutableStateOf<List<Int>>(emptyList()) }
    var elapsedSec by remember { mutableStateOf(0) }
    var inRest by remember { mutableStateOf(false) }
    var restRemaining by remember { mutableStateOf(0) }

    val repCount by vm.repCount

    // Reset on entry and start calibration immediately
    LaunchedEffect(Unit) {
        vm.reset()
        vm.startCalibration()
    }
    DisposableEffect(Unit) { onDispose { vm.reset() } }

    // Workout timer (1Hz tick)
    LaunchedEffect(Unit) {
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF080E18))) {
        CameraBackground(vm = vm)
        RunningView(
            config = config,
            repCount = repCount,
            currentSet = currentSet,
            setHistory = setHistory,
            inRest = inRest,
            restRemaining = restRemaining,
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
