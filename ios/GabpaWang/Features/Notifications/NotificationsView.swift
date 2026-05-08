import SwiftUI

/// 알림 화면. `NotificationScreen.kt` 포팅 (로컬 더미 데이터).
struct NotificationsView: View {
    @Environment(\.appColors) private var colors
    let onBack: () -> Void

    private struct Notif: Identifiable {
        let id = UUID()
        let icon: String
        let title: String
        let body: String
        let time: String
    }

    private let items: [Notif] = [
        .init(icon: "🏆", title: "1회 최고기록 갱신!", body: "방금 30회를 달성했어요. 멋져요.", time: "방금"),
        .init(icon: "⚡", title: "2단계 달성", body: "갑빠가 진화했어요. 카카오로 자랑해보세요.", time: "어제"),
        .init(icon: "🔥", title: "연속운동 위기", body: "오늘 푸시업을 안 하셨어요. 잠깐 5개만!", time: "2시간 전"),
        .init(icon: "🔔", title: "리마인더", body: "오늘도 잊지 말고 푸시업!", time: "오전 9:00"),
    ]

    var body: some View {
        VStack(spacing: 0) {
            BackHeader(title: "알림", onBack: onBack)
            ScrollView {
                LazyVStack(spacing: 8) {
                    ForEach(items) { item in
                        HStack(spacing: 12) {
                            Text(item.icon).font(.system(size: 28))
                            VStack(alignment: .leading, spacing: 2) {
                                Text(item.title)
                                    .font(.system(size: 14, weight: .bold))
                                    .foregroundStyle(colors.textPrimary)
                                Text(item.body)
                                    .font(.system(size: 12))
                                    .foregroundStyle(colors.textSub)
                                    .lineLimit(2)
                            }
                            Spacer()
                            Text(item.time)
                                .font(.system(size: 11))
                                .foregroundStyle(colors.textSub)
                        }
                        .padding(.horizontal, 14)
                        .padding(.vertical, 12)
                        .background(colors.bgCard)
                        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
            }
        }
        .background(colors.bgDark)
    }
}
