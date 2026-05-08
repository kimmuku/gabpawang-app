import SwiftUI
import AVFoundation

/// 운동 진행 — 카메라 + 자세 검출 + 카운팅. `WorkoutRunningScreen.kt` 포팅.
struct WorkoutRunningView: View {
    @Environment(\.appColors) private var colors
    @StateObject private var vm = WorkoutViewModel()

    let config: WorkoutConfig
    let voiceEnabled: Bool
    let onFinish: (WorkoutResult) -> Void

    @State private var startedAt = Date()
    @State private var setHistory: [Int] = []
    @State private var currentSet = 1
    /// 세트 종료 후 휴식 중인지.
    @State private var resting = false
    @State private var restRemaining = 60
    @State private var restTimer: Timer?
    @State private var permissionDenied = false
    @State private var showSkeleton = false

    var body: some View {
        ZStack {
            // 카메라 프리뷰 (백그라운드)
            CameraPreview(session: vm.camera.session)
                .ignoresSafeArea()
                .overlay(Color.black.opacity(0.25).ignoresSafeArea())

            if showSkeleton {
                PoseSkeletonOverlay(frame: vm.lastFrame)
            }

            // 상단 — 종료 + 세트 정보
            VStack {
                HStack {
                    Button {
                        finish(reason: .manual)
                    } label: {
                        Image(systemName: "xmark")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundStyle(.white)
                            .frame(width: 40, height: 40)
                            .background(.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                    Spacer()
                    if config.mode == "target" {
                        Text("\(currentSet) / \(config.targetSets) 세트")
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 6)
                            .background(.black.opacity(0.5))
                            .clipShape(Capsule())
                    } else if config.mode == "timed" {
                        Text(formattedTimeRemaining)
                            .font(.system(size: 14, weight: .bold))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 6)
                            .background(.black.opacity(0.5))
                            .clipShape(Capsule())
                    }
                    Spacer()
                    Button {
                        showSkeleton.toggle()
                    } label: {
                        Image(systemName: showSkeleton ? "rectangle.dashed" : "eye.slash")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundStyle(.white)
                            .frame(width: 40, height: 40)
                            .background(.black.opacity(0.5))
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.top, 8)

                Spacer()

                // 가운데 — 카운트
                if resting {
                    restingOverlay
                } else {
                    VStack(spacing: 8) {
                        Text(vm.phase)
                            .font(.system(size: 24, weight: .bold))
                            .foregroundStyle(.white.opacity(0.8))
                        Text("\(vm.repCount)")
                            .font(.system(size: 96, weight: .heavy))
                            .foregroundStyle(.white)
                            .shadow(color: .black.opacity(0.5), radius: 8)

                        if config.mode == "target" {
                            let target = config.targetCounts.indices.contains(currentSet - 1)
                                ? config.targetCounts[currentSet - 1]
                                : 20
                            Text("목표 \(target)개")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundStyle(.white.opacity(0.7))
                        }
                    }
                }

                Spacer()

                // 하단 — 시작/일시정지/+1
                HStack(spacing: 12) {
                    if !vm.isRunning {
                        PrimaryButton(title: "5초 후 시작") {
                            vm.startCalibration()
                        }
                    } else {
                        Button {
                            // 수동 +1 (모델이 놓친 경우를 위한 폴백)
                            // VM에 별도 메서드를 두지 않고, 결과 합산용 setHistory에 반영.
                            // 즉시 카운트 가산을 위해 reset 대신 임시 변수 활용.
                            manualAdd()
                        } label: {
                            Image(systemName: "plus")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundStyle(.black)
                                .frame(width: 60, height: 60)
                                .background(Color.gabpaYellow)
                                .clipShape(Circle())
                        }
                        Spacer()
                        Button {
                            advanceSet()
                        } label: {
                            Text(currentSet >= config.targetSets || config.mode != "target" ? "운동 종료" : "다음 세트")
                                .font(.system(size: 16, weight: .bold))
                                .foregroundStyle(.black)
                                .padding(.horizontal, 20)
                                .padding(.vertical, 14)
                                .background(Color.gabpaYellow)
                                .clipShape(Capsule())
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.bottom, 24)
            }

            if permissionDenied {
                permissionOverlay
            }
        }
        .task {
            do {
                try await vm.setupCamera()
            } catch {
                permissionDenied = true
            }
        }
        .onDisappear {
            vm.teardown()
            restTimer?.invalidate()
        }
    }

    // MARK: - 휴식 오버레이

    private var restingOverlay: some View {
        VStack(spacing: 16) {
            Text("휴식")
                .font(.system(size: 22, weight: .bold))
                .foregroundStyle(.white)
            Text("\(restRemaining)")
                .font(.system(size: 96, weight: .heavy))
                .foregroundStyle(.gabpaYellow)
            Button("스킵") { startNextSet() }
                .font(.system(size: 14, weight: .bold))
                .foregroundStyle(.white)
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(.black.opacity(0.5))
                .clipShape(Capsule())
        }
    }

    // MARK: - 권한 오버레이

    private var permissionOverlay: some View {
        VStack(spacing: 16) {
            Image(systemName: "camera.badge.ellipsis")
                .font(.system(size: 64))
                .foregroundStyle(.gabpaYellow)
            Text("카메라 권한이 필요합니다")
                .font(.system(size: 18, weight: .bold))
                .foregroundStyle(.white)
            Text("설정 > 갑빠왕에서 카메라 권한을 허용해주세요.")
                .font(.system(size: 13))
                .foregroundStyle(.white.opacity(0.7))
                .multilineTextAlignment(.center)
            Button {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            } label: {
                Text("설정 열기")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundStyle(.black)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.gabpaYellow)
                    .clipShape(Capsule())
            }
        }
        .padding(32)
        .background(.black.opacity(0.85))
        .clipShape(RoundedRectangle(cornerRadius: 18, style: .continuous))
        .padding(40)
    }

    // MARK: - Logic

    private var formattedTimeRemaining: String {
        let elapsed = Int(Date().timeIntervalSince(startedAt))
        let remaining = max(0, config.timedSecs - elapsed)
        return String(format: "%d:%02d", remaining / 60, remaining % 60)
    }

    private func manualAdd() {
        // VM 내부 카운트는 알고리즘이 관리하므로 상위에서는 setHistory에 +1만 더한다.
        // 표시되는 repCount는 알고리즘 결과이므로 시각적으론 다음 자세 인식 시 동기화됨.
        // 단순화: 마지막 세트 카운트를 한 번 가산하는 헬퍼.
        if setHistory.indices.contains(currentSet - 1) {
            setHistory[currentSet - 1] += 1
        } else {
            setHistory.append(1)
        }
    }

    private enum FinishReason { case manual, completed }

    private func advanceSet() {
        // 현재 세트 카운트를 history에 push.
        let finalCount = max(vm.repCount, setHistory.indices.contains(currentSet - 1) ? setHistory[currentSet - 1] : 0)
        if setHistory.indices.contains(currentSet - 1) {
            setHistory[currentSet - 1] = finalCount
        } else {
            setHistory.append(finalCount)
        }

        if config.mode == "target" && currentSet < config.targetSets {
            startRest()
        } else {
            finish(reason: .completed)
        }
    }

    private func startRest() {
        resting = true
        restRemaining = 60
        restTimer?.invalidate()
        restTimer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { _ in
            Task { @MainActor in
                if restRemaining > 0 { restRemaining -= 1 }
                if restRemaining == 0 { startNextSet() }
            }
        }
        vm.reset()
    }

    private func startNextSet() {
        restTimer?.invalidate()
        restTimer = nil
        resting = false
        currentSet += 1
        vm.startCalibration()
    }

    private func finish(reason: FinishReason) {
        // 마지막 세트 누락 보정.
        if !setHistory.indices.contains(currentSet - 1) {
            setHistory.append(vm.repCount)
        }
        let total = setHistory.reduce(0, +)
        let durationSec = Int(Date().timeIntervalSince(startedAt))
        onFinish(WorkoutResult(
            sets: max(setHistory.count, 1),
            total: total,
            history: setHistory,
            durationSec: durationSec
        ))
    }
}
