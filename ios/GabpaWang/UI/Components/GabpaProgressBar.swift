import SwiftUI

/// 옐로우 베이스 + 오렌지 "extra" 오버레이를 가진 진행도 바.
/// `GabpaProgressBar` Compose 컴포저블 포팅.
struct GabpaProgressBar: View {
    let value: Double
    let max: Double
    var extra: Double = 0
    var height: CGFloat = 6

    var body: some View {
        GeometryReader { geo in
            let capped = min(Swift.max(value, 0), max)
            let pct = max > 0 ? CGFloat(capped / max) : 0
            let extraPct = max > 0 ? min(CGFloat(Swift.max(extra, 0) / max), pct) : 0

            ZStack(alignment: .leading) {
                Capsule()
                    .fill(Color.white.opacity(0.15))
                Capsule()
                    .fill(Color.gabpaYellow)
                    .frame(width: geo.size.width * pct)
                if extraPct > 0 {
                    Capsule()
                        .fill(Color.gabpaOrange)
                        .frame(width: geo.size.width * extraPct)
                }
            }
        }
        .frame(height: height)
    }
}
