import SwiftUI

/// 뒤로가기 + 타이틀 헤더. `BackHeader` 포팅.
struct BackHeader: View {
    @Environment(\.appColors) private var colors
    let title: String
    var subtitle: String? = nil
    let onBack: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 12) {
                Button(action: onBack) {
                    ZStack {
                        Circle().fill(colors.bgCard)
                        Text("←")
                            .font(.system(size: 18, weight: .semibold))
                            .foregroundStyle(colors.textPrimary)
                    }
                    .frame(width: 36, height: 36)
                }
                Text(title)
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(colors.textPrimary)
                Spacer()
            }
            if let subtitle {
                Text(subtitle)
                    .font(.system(size: 13))
                    .foregroundStyle(colors.textSub)
                    .padding(.leading, 48)
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
    }
}

/// 섹션 타이틀.
struct SectionTitle: View {
    @Environment(\.appColors) private var colors
    let text: String
    var body: some View {
        Text(text)
            .font(.system(size: 15, weight: .bold))
            .foregroundStyle(colors.textPrimary)
            .padding(.horizontal, 16)
            .padding(.vertical, 8)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}
