import SwiftUI

/// 홈 화면. `HomeScreen.kt` 포팅.
struct HomeView: View {
    @Environment(\.appColors) private var colors

    let charStage: Int
    let totalPushups: Int
    let oneRepMax: Int
    let onNav: (Screen) -> Void
    let onStartWorkout: () -> Void
    let onCharacter: () -> Void
    let onNotifications: () -> Void

    @State private var bobOffset: CGFloat = 0
    @State private var bobScale: CGFloat = 1.0

    var body: some View {
        let curThreshold = thresholdFor(stage: charStage)
        let nextThreshold = nextThresholdFor(stage: charStage)
        let progressIntoStage = max(totalPushups - curThreshold, 0)
        let stageRange = max(nextThreshold - curThreshold, 1)
        let remaining = max(nextThreshold - totalPushups, 0)
        let ratio = stageRange > 0 ? Double(progressIntoStage) / Double(stageRange) : 0
        let pct = max(0, min(100, Int(ratio * 100)))

        VStack(spacing: 0) {
            // 상단 알림 벨
            HStack {
                Spacer()
                Button(action: onNotifications) {
                    Image(systemName: "bell")
                        .font(.system(size: 22))
                        .foregroundStyle(colors.textPrimary)
                }
                .padding(.trailing, 16)
                .padding(.top, 8)
            }

            VStack(spacing: 0) {
                Spacer(minLength: 0)

                if oneRepMax > 0 {
                    HStack(spacing: 10) {
                        homeStatCard(icon: "💪", label: "1회 최고 기록", value: "\(oneRepMax)개")
                        if let rankText = nationalRankText(maxReps: oneRepMax) {
                            homeStatCard(icon: "🏆", label: "전국 성인남자", value: rankText)
                        }
                    }
                    .padding(.horizontal, 24)
                    .padding(.bottom, 16)
                }

                Button(action: onCharacter) {
                    GabpaCharacter(stage: charStage, size: 180)
                        .offset(y: bobOffset)
                        .scaleEffect(bobScale)
                }
                .buttonStyle(.plain)
                .onAppear {
                    withAnimation(.easeInOut(duration: 1.6).repeatForever(autoreverses: true)) {
                        bobOffset = -22
                        bobScale = 1.04
                    }
                }

                Spacer().frame(height: 8)

                Text("\(charStage)단계 · \(STAGE_NAMES[charStage])")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(colors.textPrimary)

                Spacer().frame(height: 12)

                VStack(spacing: 8) {
                    HStack {
                        Text("\(charStage)단계")
                            .font(.system(size: 13))
                            .foregroundStyle(colors.textSub)
                        Spacer()
                        Text("레벨업까지 \(remaining)개")
                            .font(.system(size: 14, weight: .heavy))
                            .foregroundStyle(colors.accent)
                        Spacer()
                        Text("\(min(charStage + 1, 10))단계 🔒")
                            .font(.system(size: 13))
                            .foregroundStyle(colors.textSub)
                    }
                    ZStack {
                        GabpaProgressBar(
                            value: Double(progressIntoStage),
                            max: Double(stageRange),
                            height: 16
                        )
                        GeometryReader { geo in
                            Text("\(pct)%")
                                .font(.system(size: 9, weight: .heavy))
                                .foregroundStyle(Color(red: 0.10, green: 0.06, blue: 0))
                                .frame(width: geo.size.width * CGFloat(ratio), height: 16)
                        }
                        .frame(height: 16)
                    }
                }
                .padding(.horizontal, 24)

                Spacer(minLength: 0)
            }

            VStack(spacing: 8) {
                PrimaryButton(title: "푸쉬업 시작 💪", action: onStartWorkout)
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 8)

            BottomNav(active: .home, onNav: onNav)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(colors.bgDark)
    }

    @ViewBuilder
    private func homeStatCard(icon: String, label: String, value: String) -> some View {
        VStack(spacing: 4) {
            Text(icon).font(.system(size: 22))
            Text(label)
                .font(.system(size: 13))
                .foregroundStyle(colors.textSub)
            Text(value)
                .font(.system(size: 18, weight: .heavy))
                .foregroundStyle(colors.accent)
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(colors.bgCard)
        .overlay(
            RoundedRectangle(cornerRadius: 14, style: .continuous)
                .stroke(colors.borderCard, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
    }
}
