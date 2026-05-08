import SwiftUI

/// 첫 실행 시 표시되는 튜토리얼. `TutorialScreen.kt` 포팅.
struct TutorialView: View {
    @Environment(\.appColors) private var colors
    let onStart: () -> Void

    private let pages: [(title: String, body: String, icon: String)] = [
        (
            "푸쉬업을 자동으로 셉니다",
            "휴대폰을 벽에 세우고 본인을 비추면\nAI가 자세를 인식해 횟수를 셉니다.",
            "figure.strengthtraining.traditional"
        ),
        (
            "캐릭터가 진화합니다",
            "누적 횟수에 따라 9단계로 캐릭터가 성장.\n작은 변화가 모여 갑빠왕이 됩니다.",
            "trophy.fill"
        ),
        (
            "기록이 쌓입니다",
            "1회 최고기록·캘린더·통계로\n나의 성장이 한눈에 보입니다.",
            "chart.bar.fill"
        )
    ]
    @State private var index = 0

    var body: some View {
        VStack(spacing: 0) {
            Spacer()
            VStack(spacing: 24) {
                Image(systemName: pages[index].icon)
                    .font(.system(size: 96, weight: .regular))
                    .foregroundStyle(.gabpaYellow)
                Text(pages[index].title)
                    .font(GabpaTypography.titleXL)
                    .foregroundStyle(colors.textPrimary)
                    .multilineTextAlignment(.center)
                Text(pages[index].body)
                    .font(GabpaTypography.bodyL)
                    .foregroundStyle(colors.textSub)
                    .multilineTextAlignment(.center)
            }
            .padding(.horizontal, 24)
            Spacer()

            HStack(spacing: 8) {
                ForEach(0..<pages.count, id: \.self) { i in
                    Circle()
                        .fill(i == index ? Color.gabpaYellow : colors.borderCard)
                        .frame(width: 8, height: 8)
                }
            }
            .padding(.bottom, 32)

            VStack(spacing: 12) {
                PrimaryButton(title: index == pages.count - 1 ? "시작하기" : "다음") {
                    if index < pages.count - 1 {
                        withAnimation { index += 1 }
                    } else {
                        onStart()
                    }
                }
                if index < pages.count - 1 {
                    GhostButton(title: "건너뛰기") { onStart() }
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(colors.bgDark)
    }
}
