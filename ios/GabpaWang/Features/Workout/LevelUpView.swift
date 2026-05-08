import SwiftUI

/// 레벨업 축하 화면. `LevelUpScreen.kt` 포팅.
struct LevelUpView: View {
    @Environment(\.appColors) private var colors
    let newStage: Int
    let onNext: () -> Void

    @State private var sparkle = false

    var body: some View {
        VStack(spacing: 16) {
            Spacer()
            ZStack {
                Circle()
                    .fill(LinearGradient(
                        colors: [.gabpaYellow.opacity(0.3), .gabpaOrange.opacity(0.15)],
                        startPoint: .top, endPoint: .bottom
                    ))
                    .frame(width: 280, height: 280)
                    .blur(radius: 30)
                    .scaleEffect(sparkle ? 1.1 : 1.0)
                GabpaCharacter(stage: newStage, size: 220, glow: true)
            }
            .onAppear {
                withAnimation(.easeInOut(duration: 1.5).repeatForever(autoreverses: true)) {
                    sparkle = true
                }
            }

            Text("\(newStage)단계 달성!")
                .font(.system(size: 32, weight: .heavy))
                .foregroundStyle(.gabpaYellow)

            Text(STAGE_NAMES[newStage])
                .font(.system(size: 20, weight: .bold))
                .foregroundStyle(colors.textPrimary)

            Text(STAGE_SUBTITLES[newStage])
                .font(.system(size: 14))
                .foregroundStyle(colors.textSub)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 32)

            Text("누적 \(thresholdFor(stage: newStage))개 돌파!")
                .font(.system(size: 13))
                .foregroundStyle(colors.textSub)
                .padding(.top, 4)

            Spacer()

            VStack(spacing: 10) {
                PrimaryButton(title: "계속 성장하기", action: onNext)
                GhostButton(title: "카카오로 자랑하기") {
                    // TODO: KakaoLink — Edge Function or Kakao Share SDK 연동.
                    // 일단 onNext와 동일.
                    onNext()
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)
        }
        .background(colors.bgDark)
    }
}
