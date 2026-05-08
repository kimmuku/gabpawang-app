import SwiftUI

/// 9단계 캐릭터 일러스트. `GabpaChar` 포팅.
///
/// 실제 일러스트는 `Assets.xcassets`에 `char_stage_1`~`char_stage_10` 이미지로
/// 추가해야 함. 에셋이 없는 경우 임시 SF Symbol 플레이스홀더가 표시됨.
struct GabpaCharacter: View {
    let stage: Int
    let size: CGFloat
    var glow: Bool = false

    private var clamped: Int { min(max(stage, 1), 10) }
    private var assetName: String { "char_stage_\(clamped)" }

    var body: some View {
        Group {
            if let _ = UIImage(named: assetName) {
                Image(assetName)
                    .resizable()
                    .scaledToFit()
            } else {
                // 에셋 미준비 시 폴백 — 단계별로 약간 다른 톤의 원형 표시.
                ZStack {
                    Circle()
                        .fill(LinearGradient(
                            colors: [.gabpaYellow.opacity(0.3), .gabpaOrange.opacity(0.2)],
                            startPoint: .top,
                            endPoint: .bottom
                        ))
                    Image(systemName: "figure.strengthtraining.traditional")
                        .resizable()
                        .scaledToFit()
                        .padding(size * 0.18)
                        .foregroundStyle(.gabpaYellow)
                    VStack {
                        Spacer()
                        Text("\(clamped)단계")
                            .font(.system(size: max(11, size * 0.06), weight: .heavy))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(.black.opacity(0.55))
                            .clipShape(Capsule())
                            .padding(.bottom, size * 0.08)
                    }
                }
            }
        }
        .frame(width: size, height: size)
        .shadow(color: glow ? .gabpaYellow.opacity(0.5) : .clear, radius: glow ? 20 : 0)
    }
}
