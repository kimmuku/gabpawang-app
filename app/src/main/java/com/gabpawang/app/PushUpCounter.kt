package com.gabpawang.app

import java.util.ArrayDeque

enum class PushUpState { IDLE, WARMING, UP, DOWN }

class PushUpCounter {
    var count = 0
        private set
    var state = PushUpState.IDLE
        private set
    var yLow = 0.5f
        private set
    var yHigh = 0.5f
        private set

    private val smoothBuf = ArrayDeque<Float>()
    private val window = ArrayDeque<Float>()
    private var lastSmoothed = 0f
    private var stateFrames = 0

    companion object {
        private const val SMOOTH_WINDOW = 5
        private const val WINDOW_FRAMES = 120   // 4s at 30fps
        private const val MIN_INIT_FRAMES = 45  // ~1.5s warmup after countdown ends
        private const val DOWN_FRACTION = 0.45f
        private const val UP_FRACTION = 0.20f
        private const val MIN_BAND = 0.06f
        private const val PERCENTILE_LOW = 0.15f
        private const val PERCENTILE_HIGH = 0.85f
        private const val MIN_UP_FRAMES = 9
        private const val MIN_DOWN_FRAMES = 11
        // Seeded band on warmup exit so the first rep counts before DOWN frames accumulate
        private const val INIT_BAND_SEED = 0.15f
    }

    fun start() {
        state = PushUpState.WARMING
        count = 0
        smoothBuf.clear()
        window.clear()
        lastSmoothed = 0f
        stateFrames = 0
        yLow = 0.5f
        yHigh = 0.5f
    }

    fun reset() {
        state = PushUpState.IDLE
        count = 0
        smoothBuf.clear()
        window.clear()
        lastSmoothed = 0f
        stateFrames = 0
        yLow = 0.5f
        yHigh = 0.5f
    }

    fun process(shoulderY: Float): Boolean {
        if (state == PushUpState.IDLE) return false

        val s = smooth(shoulderY).also { lastSmoothed = it }
        if (window.size >= WINDOW_FRAMES) window.poll()
        window.add(s)

        if (window.size < MIN_INIT_FRAMES) {
            stateFrames++
            return false
        }

        if (state == PushUpState.WARMING) {
            // Seed the initial band so the first rep is counted before DOWN frames
            // accumulate in the rolling window; without this, band stays near 0 until
            // the user has completed one full rep.
            val wSorted = window.sorted()
            val wn = wSorted.size
            yLow = wSorted[(wn * PERCENTILE_LOW).toInt()]
            yHigh = yLow + INIT_BAND_SEED
            state = PushUpState.UP
            stateFrames = 0
        }

        val sorted = window.sorted()
        val n = sorted.size
        val yl = sorted[(n * PERCENTILE_LOW).toInt()]
        // Maintain at least INIT_BAND_SEED band until real DOWN frames push yHigh higher
        val yh = maxOf(sorted[(n * PERCENTILE_HIGH).toInt()], yl + INIT_BAND_SEED)
        val band = yh - yl

        yLow = yl
        yHigh = yh

        if (band < MIN_BAND) {
            stateFrames++
            return false
        }

        val downThr = yl + DOWN_FRACTION * band
        val upThr   = yl + UP_FRACTION * band

        return when (state) {
            PushUpState.UP -> {
                stateFrames++
                if (s > downThr && stateFrames >= MIN_UP_FRAMES) {
                    state = PushUpState.DOWN
                    stateFrames = 0
                }
                false
            }
            PushUpState.DOWN -> {
                stateFrames++
                if (stateFrames >= MIN_DOWN_FRAMES && s < upThr) {
                    state = PushUpState.UP
                    count++
                    stateFrames = 0
                    true
                } else false
            }
            else -> false
        }
    }

    fun currentDisplacement(): Float = lastSmoothed - yLow

    fun downThresholdValue(): Float {
        val band = yHigh - yLow
        return yLow + DOWN_FRACTION * band
    }

    fun upThresholdValue(): Float {
        val band = yHigh - yLow
        return yLow + UP_FRACTION * band
    }

    fun warmupProgress(): Float =
        if (state == PushUpState.WARMING) window.size.toFloat() / MIN_INIT_FRAMES
        else 1f

    private fun smooth(y: Float): Float {
        if (smoothBuf.size >= SMOOTH_WINDOW) smoothBuf.poll()
        smoothBuf.add(y)
        return smoothBuf.average().toFloat()
    }
}
