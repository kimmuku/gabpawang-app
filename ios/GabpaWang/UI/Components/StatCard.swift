import SwiftUI

/// `StatCard` 포팅. 큰 숫자 + 라벨.
struct StatCard: View {
    @Environment(\.appColors) private var colors
    let value: String
    let label: String
    var color: Color? = nil
    var onTap: (() -> Void)? = nil

    var body: some View {
        let card = VStack(spacing: 4) {
            Text(value)
                .font(GabpaTypography.stat)
                .foregroundStyle(color ?? colors.textPrimary)
            Text(label)
                .font(.system(size: 11))
                .foregroundStyle(colors.textSub)
        }
        .padding(.vertical, 16)
        .padding(.horizontal, 12)
        .frame(maxWidth: .infinity)
        .background(colors.bgCard)
        .overlay(
            RoundedRectangle(cornerRadius: GabpaRadius.medium, style: .continuous)
                .stroke(colors.borderCard, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: GabpaRadius.medium, style: .continuous))

        if let onTap {
            Button(action: onTap) { card }.buttonStyle(.plain)
        } else {
            card
        }
    }
}
