import SwiftUI
import Combine
import AVFoundation

/// 운동 진행 ViewModel. `MainViewModel.kt`(이름은 카메라/카운터 VM) 포팅.
@MainActor
final class WorkoutViewModel: ObservableObject {

    // 카운터 / 상태
    @Published private(set) var repCount: Int = 0
    @Published private(set) var phase: String = "준비"
    @Published private(set) var isRunning = false
    @Published private(set) var isCalibrating = false
    @Published private(set) var calibrationProgress: Float = 0
    @Published private(set) var poseDetected = false
    @Published private(set) var bodyInFrame = false

    // 디버그/오버레이용
    @Published private(set) var debugCurrentY: Float = 0
    @Published private(set) var debugBaselineY: Float = 0
    @Published private(set) var debugDelta: Float = 0
    @Published private(set) var debugDownThreshold: Float = 0
    @Published private(set) var debugUpThreshold: Float = 0
    @Published private(set) var lastFrame: PoseLandmarkerHelper.PoseFrame?

    let camera = CameraSession()
    private let counter = PushUpCounter()
    private let poseHelper = PoseLandmarkerHelper()

    // 카운트다운/홀드오버 상태
    private var countdownTask: Task<Void, Never>?
    private var isInCountdown = false
    private var lastShoulderY: Float?
    private var noDetectFrames = 0
    private let HOLDOVER_FRAMES = 30 // ~1.0s @ 30fps

    init() {
        poseHelper.onResult = { [weak self] frame in
            // Vision 콜백은 백그라운드 큐. UI 갱신은 메인으로.
            Task { @MainActor [weak self] in
                self?.onPose(frame)
            }
        }
        camera.onFrame = { [weak self] sample in
            self?.poseHelper.analyze(sampleBuffer: sample, orientation: .leftMirrored)
        }
    }

    // MARK: - Lifecycle

    func setupCamera() async throws {
        let granted = await CameraSession.requestPermission()
        guard granted else {
            throw NSError(domain: "WorkoutViewModel", code: 1,
                          userInfo: [NSLocalizedDescriptionKey: "카메라 권한이 없습니다."])
        }
        try camera.configure(position: .front)
        camera.start()
    }

    func teardown() {
        countdownTask?.cancel()
        camera.stop()
    }

    // MARK: - 카운트다운 → 카운팅 시작

    func startCalibration() {
        countdownTask?.cancel()
        isInCountdown = true
        isRunning = true
        isCalibrating = false
        phase = "5"
        repCount = 0
        poseDetected = false
        lastShoulderY = nil
        noDetectFrames = 0

        countdownTask = Task { [weak self] in
            for i in stride(from: 5, through: 1, by: -1) {
                guard !Task.isCancelled else { return }
                await MainActor.run { self?.phase = "\(i)" }
                try? await Task.sleep(for: .seconds(1))
            }
            await MainActor.run {
                self?.isInCountdown = false
                self?.counter.start()
            }
        }
    }

    func reset() {
        countdownTask?.cancel()
        countdownTask = nil
        isInCountdown = false
        counter.reset()
        isRunning = false
        isCalibrating = false
        phase = "준비"
        repCount = 0
        calibrationProgress = 0
        poseDetected = false
        bodyInFrame = false
        lastFrame = nil
        lastShoulderY = nil
        noDetectFrames = 0
    }

    // MARK: - 자세 처리

    private func onPose(_ frame: PoseLandmarkerHelper.PoseFrame) {
        let hasPose = frame.hasShoulders
        poseDetected = hasPose
        lastFrame = frame

        let midShoulderY: Float
        if hasPose, let mid = frame.midShoulderY {
            midShoulderY = mid
            lastShoulderY = mid
            noDetectFrames = 0
        } else {
            noDetectFrames += 1
            guard let held = lastShoulderY, noDetectFrames <= HOLDOVER_FRAMES else { return }
            midShoulderY = held
        }

        debugCurrentY = midShoulderY
        debugBaselineY = counter.yLow
        debugDelta = counter.currentDisplacement()
        debugDownThreshold = counter.downThresholdValue()
        debugUpThreshold = counter.upThresholdValue()

        guard isRunning, !isInCountdown else { return }

        if hasPose {
            bodyInFrame = frame.hipsVisible
            if !frame.hipsVisible { return }
        }

        _ = counter.process(shoulderY: midShoulderY)

        repCount = counter.count
        calibrationProgress = counter.warmupProgress()
        isCalibrating = (counter.state == .warming)

        switch counter.state {
        case .warming:
            phase = "준비 중... \(Int(counter.warmupProgress() * 100))%"
        case .up:   phase = "UP ↑"
        case .down: phase = "DOWN ↓"
        case .idle: phase = "준비"
        }
    }
}
