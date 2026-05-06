package com.gabpawang.app

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

// Bridges CameraX ImageAnalysis frames to MediaPipe PoseLandmarker.
// Requires ImageAnalysis built with OUTPUT_IMAGE_FORMAT_RGBA_8888.
class PoseAnalyzer(
    private val helper: PoseLandmarkerHelper
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        // helper.detectAsync owns the imageProxy lifecycle (closes it internally)
        helper.detectAsync(imageProxy)
    }
}
