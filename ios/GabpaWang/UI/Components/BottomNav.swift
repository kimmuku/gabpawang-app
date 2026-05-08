import SwiftUI

/// 하단 네비게이션 — 홈/기록/설정 3탭. `BottomNav` 포팅.
struct BottomNav: View {
    @Environment(\.appColors) private var colors
    let active: Screen
    let onNav: (Screen) -> Void

    private struct Item {
        let id: Screen
        let label: String
        let systemIcon: String
    }
    private let items: [Item] = [
        .init(id: .home, label: "홈", systemIcon: "house"),
        .init(id: .record, label: "기록", systemIcon: "chart.bar"),
        .init(id: .settings, label: "설정", systemIcon: "gearshape"),
    ]

    var body: some View {
        HStack {
            ForEach(items, id: \.id) { item in
                let tint = item.id == active ? colors.accent : colors.textSub
                Button {
                    onNav(item.id)
                } label: {
                    VStack(spacing: 2) {
                        Image(systemName: item.systemIcon)
                            .font(.system(size: 22, weight: .regular))
                            .foregroundStyle(tint)
                        Text(item.label)
                            .font(.system(size: 10))
                            .foregroundStyle(tint)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 4)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 10)
        .background(colors.bgDark.opacity(0.98))
        .overlay(
            Rectangle().frame(height: 0.5).foregroundStyle(colors.borderCard),
            alignment: .top
        )
    }
}
