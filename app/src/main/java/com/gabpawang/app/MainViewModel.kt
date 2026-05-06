package com.gabpawang.app

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class ModelState {
    data object Loading : ModelState()
    data class Downloading(val progress: Float) : ModelState()
    data object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    var modelState = mutableStateOf<ModelState>(ModelState.Loading)
        private set

    var repCount = mutableIntStateOf(0)
        private set
    var phase = mutableStateOf("준비")
        private set
    var isRunning = mutableStateOf(false)
        private set
    var isCalibrating = mutableStateOf(false)
        private set
    var calibrationProgress = mutableFloatStateOf(0f)
        private set
    var poseDetected = mutableStateOf(false)
        private set
    var bodyInFrame = mutableStateOf(false)
        private set
    var debugCurrentY = mutableFloatStateOf(0f)
        private set
    var debugBaselineY = mutableFloatStateOf(0f)
        private set
    var debugDelta = mutableFloatStateOf(0f)
        private set
    var debugDownThreshold = mutableFloatStateOf(0f)
        private set
    var debugUpThreshold = mutableFloatStateOf(0f)
        private set
    var landmarks = mutableStateOf<List<NormalizedLandmark>?>(null)
        private set

    private val counter = PushUpCounter()
    private var _helper: PoseLandmarkerHelper? = null

    // Holdover: reuse last valid shoulder Y when pose briefly disappears
    private var lastShoulderY: Float? = null
    private var noDetectFrames = 0
    private val HOLDOVER_FRAMES = 30  // ~1.0s at 30fps — covers full DOWN phase

    private var countdownJob: Job? = null
    private var isInCountdown = false

    fun getHelper(): PoseLandmarkerHelper? = _helper

    init {
        loadModel()
    }

    fun loadModel() {
        modelState.value = ModelState.Loading
        _helper?.close()
        _helper = null
        viewModelScope.launch {
            try {
                val helper = PoseLandmarkerHelper(
                    context = getApplication(),
                    onResults = ::onLandmarksDetected,
                    onError = { msg -> modelState.value = ModelState.Error(msg) }
                )
                helper.setup(onProgress = { pct ->
                    modelState.value = ModelState.Downloading(pct)
                })
                _helper = helper
                modelState.value = ModelState.Ready
            } catch (e: Exception) {
                modelState.value = ModelState.Error(e.message ?: "모델 로드 실패")
            }
        }
    }

    fun startCalibration() {
        countdownJob?.cancel()
        isInCountdown = true
        isRunning.value = true
        isCalibrating.value = false
        phase.value = "5"
        repCount.intValue = 0
        poseDetected.value = false
        lastShoulderY = null
        noDetectFrames = 0

        countdownJob = viewModelScope.launch {
            for (i in 5 downTo 1) {
                phase.value = "$i"
                delay(1000L)
            }
            // Countdown done — user is in push-up position. Start algorithm fresh.
            isInCountdown = false
            counter.start()
        }
    }

    fun reset() {
        countdownJob?.cancel()
        countdownJob = null
        isInCountdown = false
        counter.reset()
        isRunning.value = false
        isCalibrating.value = false
        phase.value = "준비"
        repCount.intValue = 0
        calibrationProgress.floatValue = 0f
        poseDetected.value = false
        bodyInFrame.value = false
        landmarks.value = null
        lastShoulderY = null
        noDetectFrames = 0
    }

    private fun onLandmarksDetected(result: PoseLandmarkerResult) {
        val hasPose = result.landmarks().isNotEmpty()
        poseDetected.value = hasPose
        landmarks.value = if (hasPose) result.landmarks()[0] else null

        val midShoulderY: Float

        if (hasPose) {
            val lm = result.landmarks()[0]
            // No visibility filter — shoulders face the camera throughout push-ups.
            // Visibility score drops when head tilts down, but coordinates remain valid.
            val shoulders = listOfNotNull(lm.getOrNull(11), lm.getOrNull(12))
            if (shoulders.isEmpty()) {
                noDetectFrames++
                val held = lastShoulderY ?: return
                if (noDetectFrames > HOLDOVER_FRAMES) return
                midShoulderY = held
            } else {
                midShoulderY = shoulders.map { it.y() }.average().toFloat()
                lastShoulderY = midShoulderY
                noDetectFrames = 0
            }
        } else {
            // No pose at all — use holdover if available
            noDetectFrames++
            val held = lastShoulderY ?: return
            if (noDetectFrames > HOLDOVER_FRAMES) return
            midShoulderY = held
        }

        debugCurrentY.floatValue = midShoulderY
        debugBaselineY.floatValue = counter.yLow
        debugDelta.floatValue = counter.currentDisplacement()
        debugDownThreshold.floatValue = counter.downThresholdValue()
        debugUpThreshold.floatValue = counter.upThresholdValue()

        if (hasPose) {
            val lm2 = result.landmarks()[0]
            val vis11 = lm2.getOrNull(11)?.visibility()?.orElse(-1f) ?: -1f
            val vis12 = lm2.getOrNull(12)?.visibility()?.orElse(-1f) ?: -1f
            Log.d("PushUp", "Y=%.3f vis=[%.2f,%.2f] delta=%.3f thr>=%.3f state=%s h=%d".format(
                midShoulderY, vis11, vis12, counter.currentDisplacement(),
                counter.downThresholdValue(), counter.state, noDetectFrames
            ))
        } else {
            Log.d("PushUp", "Y=%.3f(hold) delta=%.3f state=%s h=%d".format(
                midShoulderY, counter.currentDisplacement(), counter.state, noDetectFrames
            ))
        }

        if (!isRunning.value) return
        if (isInCountdown) return

        // Require hips visible — confirms shoulders + hips both in frame (proper push-up position)
        if (hasPose) {
            val lm = result.landmarks()[0]
            val hipsVisible = listOfNotNull(lm.getOrNull(23), lm.getOrNull(24))
                .any { it.visibility().orElse(0f) > 0.1f }
            bodyInFrame.value = hipsVisible
            if (!hipsVisible) return
        }

        counter.process(midShoulderY)

        repCount.intValue = counter.count
        calibrationProgress.floatValue = counter.warmupProgress()
        isCalibrating.value = counter.state == PushUpState.WARMING
        debugBaselineY.floatValue = counter.yLow
        debugDelta.floatValue = counter.currentDisplacement()
        debugDownThreshold.floatValue = counter.downThresholdValue()
        debugUpThreshold.floatValue = counter.upThresholdValue()

        phase.value = when (counter.state) {
            PushUpState.WARMING ->
                "준비 중... ${(counter.warmupProgress() * 100).toInt()}%"
            PushUpState.UP -> "UP ↑"
            PushUpState.DOWN -> "DOWN ↓"
            PushUpState.IDLE -> "준비"
        }
    }

    override fun onCleared() {
        super.onCleared()
        _helper?.close()
    }
}
