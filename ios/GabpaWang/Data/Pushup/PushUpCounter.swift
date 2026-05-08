import Foundation

enum PushUpState {
    case idle, warming, up, down
}

/// 어깨 Y 좌표 시계열로 푸시업 횟수를 세는 순수 알고리즘.
/// `PushUpCounter.kt`를 1:1 포팅 — 모든 상수와 절차 동일.
final class PushUpCounter {
    private(set) var count: Int = 0
    private(set) var state: PushUpState = .idle
    private(set) var yLow: Float = 0.5
    private(set) var yHigh: Float = 0.5

    private var smoothBuf: [Float] = []
    private var window: [Float] = []
    private var lastSmoothed: Float = 0
    private var stateFrames = 0

    private let SMOOTH_WINDOW = 5
    private let WINDOW_FRAMES = 120     // 4s @ 30fps
    private let MIN_INIT_FRAMES = 45    // ~1.5s warmup
    private let DOWN_FRACTION: Float = 0.45
    private let UP_FRACTION: Float = 0.20
    private let MIN_BAND: Float = 0.06
    private let PERCENTILE_LOW: Float = 0.15
    private let PERCENTILE_HIGH: Float = 0.85
    private let MIN_UP_FRAMES = 9
    private let MIN_DOWN_FRAMES = 11

    func start() {
        state = .warming
        count = 0
        smoothBuf.removeAll(keepingCapacity: true)
        window.removeAll(keepingCapacity: true)
        lastSmoothed = 0
        stateFrames = 0
        yLow = 0.5
        yHigh = 0.5
    }

    func reset() {
        state = .idle
        count = 0
        smoothBuf.removeAll(keepingCapacity: true)
        window.removeAll(keepingCapacity: true)
        lastSmoothed = 0
        stateFrames = 0
        yLow = 0.5
        yHigh = 0.5
    }

    /// 새 어깨 Y 입력. true 반환 시 카운트가 1 증가했다는 뜻.
    @discardableResult
    func process(shoulderY: Float) -> Bool {
        guard state != .idle else { return false }

        let s = smooth(shoulderY)
        lastSmoothed = s
        if window.count >= WINDOW_FRAMES { window.removeFirst() }
        window.append(s)

        if window.count < MIN_INIT_FRAMES {
            stateFrames += 1
            return false
        }

        if state == .warming {
            state = .up
            stateFrames = 0
        }

        let sorted = window.sorted()
        let n = sorted.count
        let yl = sorted[Int(Float(n) * PERCENTILE_LOW)]
        let yh = sorted[Int(Float(n) * PERCENTILE_HIGH)]
        let band = yh - yl

        yLow = yl
        yHigh = yh

        guard band >= MIN_BAND else {
            stateFrames += 1
            return false
        }

        let downThr = yl + DOWN_FRACTION * band
        let upThr   = yl + UP_FRACTION * band

        switch state {
        case .up:
            stateFrames += 1
            if s > downThr && stateFrames >= MIN_UP_FRAMES {
                state = .down
                stateFrames = 0
            }
            return false

        case .down:
            stateFrames += 1
            if stateFrames >= MIN_DOWN_FRAMES && s < upThr {
                state = .up
                count += 1
                stateFrames = 0
                return true
            }
            return false

        default:
            return false
        }
    }

    func currentDisplacement() -> Float { lastSmoothed - yLow }

    func downThresholdValue() -> Float {
        let band = yHigh - yLow
        return yLow + DOWN_FRACTION * band
    }

    func upThresholdValue() -> Float {
        let band = yHigh - yLow
        return yLow + UP_FRACTION * band
    }

    func warmupProgress() -> Float {
        state == .warming ? Float(window.count) / Float(MIN_INIT_FRAMES) : 1
    }

    private func smooth(_ y: Float) -> Float {
        if smoothBuf.count >= SMOOTH_WINDOW { smoothBuf.removeFirst() }
        smoothBuf.append(y)
        let sum = smoothBuf.reduce(0, +)
        return sum / Float(smoothBuf.count)
    }
}
