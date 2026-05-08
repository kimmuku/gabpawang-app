import SwiftUI

/// 챌린지 화면. `ChallengeScreen.kt` 포팅. 안드로이드와 동일하게 더미 데이터 stub.
///
/// 주의: 안드로이드 `AppRouter`에도 챌린지는 라우팅 등록만 되어 있고 실제 진입 경로는
/// 없는 상태(미완성). iOS도 동일하게 파일만 존재 — 추후 BottomNav 또는 별도 진입점에서
/// `.go(.challenge)`를 호출하도록 연결할 수 있다.
struct ChallengeView: View {
    @Environment(\.appColors) private var colors
    let onBack: () -> Void

    private struct Item: Identifiable {
        let id = UUID()
        let emoji: String
        let title: String
        let desc: String
        let progress: Double
        let current: Int
        let total: Int
        let reward: String
    }

    private let items: [Item] = [
        .init(emoji: "💯", title: "100개 챌린지", desc: "한 번에 100개에 도전!",
              progress: 0, current: 0, total: 100, reward: "골드 뱃지"),
        .init(emoji: "📅", title: "30일 챌린지", desc: "30일 연속 운동 도전",
              progress: 0.4, current: 12, total: 30, reward: "다이아 뱃지"),
        .init(emoji: "⚡", title: "스피드 챌린지", desc: "5분 안에 50개",
              progress: 0.6, current: 30, total: 50, reward: "스피드 뱃지")
    ]

    var body: some View {
        VStack(spacing: 0) {
            BackHeader(title: "챌린지", subtitle: "도전하고 보상을 받으세요 🎁", onBack: onBack)

            ScrollView {
                VStack(spacing: 12) {
                    ForEach(items) { card($0) }
                    Spacer().frame(height: 40)
                }
                .padding(.horizontal, 20)
            }
        }
        .background(colors.bgDark)
    }

    @ViewBuilder
    private func card(_ c: Item) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 12) {
                Text(c.emoji).font(.system(size: 32))
                VStack(alignment: .leading, spacing: 2) {
                    Text(c.title)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundStyle(colors.textPrimary)
                    Text(c.desc)
                        .font(.system(size: 12))
                        .foregroundStyle(colors.textSub)
                }
                Spacer()
            }
            if c.progress > 0 {
                Spacer().frame(height: 12)
                HStack {
                    Text("\(c.current) / \(c.total)")
                        .font(.system(size: 12))
                        .foregroundStyle(colors.textPrimary)
                    Spacer()
                    Text("\(Int(c.progress * 100))%")
                        .font(.system(size: 12))
                        .foregroundStyle(colors.accent)
                }
                Spacer().frame(height: 6)
                GabpaProgressBar(value: c.progress, max: 1, height: 6)
            }
            Spacer().frame(height: 10)
            Text("🎁 보상: \(c.reward)")
                .font(.system(size: 11))
                .foregroundStyle(colors.accent)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(colors.bgCard)
        .overlay(
            RoundedRectangle(cornerRadius: 16, style: .continuous)
                .stroke(colors.borderCard, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
    }
}
