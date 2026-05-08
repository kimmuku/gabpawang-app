import SwiftUI

/// `BtnPrimary` 대응. 옐로우 풀폭 버튼.
struct PrimaryButton: View {
    let title: String
    var enabled: Bool = true
    var color: Color = .gabpaYellow
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(GabpaTypography.buttonL)
                .foregroundStyle(.black)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(enabled ? color : color.opacity(0.4))
                .clipShape(RoundedRectangle(cornerRadius: GabpaRadius.medium, style: .continuous))
        }
        .disabled(!enabled)
    }
}

/// `BtnGhost` 대응. 보더 + 투명 배경.
struct GhostButton: View {
    @Environment(\.appColors) private var colors
    let title: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 15, weight: .medium))
                .foregroundStyle(colors.textPrimary)
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .overlay(
                    RoundedRectangle(cornerRadius: GabpaRadius.medium, style: .continuous)
                        .stroke(colors.borderCard, lineWidth: 1)
                )
        }
    }
}
