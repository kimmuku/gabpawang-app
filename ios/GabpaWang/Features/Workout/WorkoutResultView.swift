import SwiftUI

/// 운동 결과. `WorkoutResultScreen.kt` 포팅.
struct WorkoutResultView: View {
    @Environment(\.appColors) private var colors
    let result: WorkoutResult
    let charStage: Int
    let totalPushups: Int
    /// (조정된 총합, 조정된 세트 history) 콜백.
    let onHome: (Int, [Int]) -> Void

    @State private var adjusted: [Int] = []

    var body: some View {
        let history = adjusted.isEmpty ? result.history : adjusted
        let total = history.reduce(0, +)
        let curThreshold = thresholdFor(stage: charStage)
        let nextThreshold = nextThresholdFor(stage: charStage)
        let progressIntoStage = max(totalPushups + total - curThreshold, 0)
        let stageRange = max(nextThreshold - curThreshold, 1)
        let remaining = max(nextThreshold - (totalPushups + total), 0)

        VStack(spacing: 0) {
            ScrollView {
                VStack(spacing: 16) {
                    Text("운동 완료!")
                        .font(GabpaTypography.titleXL)
                        .foregroundStyle(colors.textPrimary)
                        .padding(.top, 24)

                    GabpaCharacter(stage: charStage, size: 140)

                    HStack(spacing: 12) {
                        StatCard(value: "\(total)", label: "총 횟수")
                        StatCard(value: "\(history.count)", label: "세트")
                        StatCard(value: durationLabel, label: "시간")
                    }
                    .padding(.horizontal, 20)

                    if !history.isEmpty {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("세트별")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(colors.textPrimary)
                            ForEach(history.indices, id: \.self) { idx in
                                HStack {
                                    Text("\(idx + 1)세트")
                                        .font(.system(size: 13))
                                        .foregroundStyle(colors.textSub)
                                    Spacer()
                                    Text("\(history[idx])회")
                                        .font(.system(size: 14, weight: .bold))
                                        .foregroundStyle(colors.textPrimary)
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 10)
                                .background(colors.bgCard)
                                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                            }
                        }
                        .padding(.horizontal, 20)
                    }

                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("\(charStage)단계")
                                .foregroundStyle(colors.textSub)
                                .font(.system(size: 13))
                            Spacer()
                            Text("레벨업까지 \(remaining)개")
                                .foregroundStyle(colors.accent)
                                .font(.system(size: 14, weight: .heavy))
                        }
                        GabpaProgressBar(
                            value: Double(progressIntoStage),
                            max: Double(stageRange),
                            extra: Double(total),
                            height: 14
                        )
                    }
                    .padding(.horizontal, 24)
                }
                .padding(.bottom, 24)
            }

            PrimaryButton(title: "홈으로") {
                onHome(total, history)
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
        .background(colors.bgDark)
        .onAppear {
            adjusted = result.history
        }
    }

    private var durationLabel: String {
        let s = result.durationSec
        return String(format: "%d:%02d", s / 60, s % 60)
    }
}
