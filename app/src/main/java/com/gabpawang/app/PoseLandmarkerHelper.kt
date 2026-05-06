package com.gabpawang.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer

class PoseLandmarkerHelper(
    private val context: Context,
    private val onResults: (PoseLandmarkerResult) -> Unit,
    private val onError: (String) -> Unit
) {
    private var poseLandmarker: PoseLandmarker? = null
    private var modelBuffer: ByteBuffer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val MODEL_FILENAME = "pose_landmarker_lite.task"
        private const val MODEL_URL =
            "https://storage.googleapis.com/mediapipe-models/pose_landmarker/" +
                    "pose_landmarker_lite/float16/latest/pose_landmarker_lite.task"
        const val MIN_DETECTION_CONFIDENCE = 0.3f
        const val MIN_TRACKING_CONFIDENCE = 0.3f
    }

    fun isInitialized(): Boolean = poseLandmarker != null

    // Call from a coroutine (e.g. viewModelScope).
    // onProgress: 0.0-1.0 during download, called on main thread.
    suspend fun setup(onProgress: (Float) -> Unit) {
        val modelFile = File(context.filesDir, MODEL_FILENAME)
        if (!modelFile.exists()) {
            downloadModel(modelFile, onProgress)
        } else {
            withContext(Dispatchers.Main) { onProgress(1f) }
        }
        withContext(Dispatchers.IO) {
            initPoseLandmarker(modelFile)
        }
    }

    private suspend fun downloadModel(output: File, onProgress: (Float) -> Unit) =
        withContext(Dispatchers.IO) {
            val tempFile = File(context.filesDir, "$MODEL_FILENAME.tmp")
            try {
                val connection = URL(MODEL_URL).openConnection() as HttpURLConnection
                connection.connectTimeout = 15_000
                connection.readTimeout = 30_000
                connection.connect()
                val total = connection.contentLength.toLong()
                var downloaded = 0L

                BufferedInputStream(connection.inputStream, 8192).use { input ->
                    FileOutputStream(tempFile).use { out ->
                        val buf = ByteArray(8192)
                        var bytes: Int
                        while (input.read(buf).also { bytes = it } != -1) {
                            out.write(buf, 0, bytes)
                            downloaded += bytes
                            if (total > 0) {
                                val pct = downloaded.toFloat() / total
                                withContext(Dispatchers.Main) { onProgress(pct) }
                            }
                        }
                    }
                }
                tempFile.renameTo(output)
                withContext(Dispatchers.Main) { onProgress(1f) }
            } catch (e: Exception) {
                tempFile.delete()
                throw e
            }
        }

    private fun initPoseLandmarker(modelFile: File) {
        val bytes = modelFile.readBytes()
        val buf = ByteBuffer.allocateDirect(bytes.size).also { it.put(bytes); it.rewind() }
        modelBuffer = buf

        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(
                BaseOptions.builder()
                    .setModelAssetBuffer(buf)
                    .build()
            )
            .setMinPoseDetectionConfidence(MIN_DETECTION_CONFIDENCE)
            .setMinPosePresenceConfidence(MIN_DETECTION_CONFIDENCE)
            .setMinTrackingConfidence(MIN_TRACKING_CONFIDENCE)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result: PoseLandmarkerResult, _: MPImage ->
                mainHandler.post { onResults(result) }
            }
            .setErrorListener { e: RuntimeException ->
                mainHandler.post { onError(e.message ?: "MediaPipe error") }
            }
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    // Called from CameraX analyzer thread. Closes imageProxy internally.
    fun detectAsync(imageProxy: ImageProxy) {
        if (!isInitialized()) {
            imageProxy.close()
            return
        }
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val bitmap = Bitmap.createBitmap(
            imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        imageProxy.close()

        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        val rotated = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        poseLandmarker?.detectAsync(
            BitmapImageBuilder(rotated).build(),
            SystemClock.uptimeMillis()
        )
    }

    fun close() {
        poseLandmarker?.close()
        poseLandmarker = null
        modelBuffer = null
    }
}
