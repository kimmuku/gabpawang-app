import SwiftUI

/// 디버그용 포즈 스켈레톤 오버레이. `SkeletonOverlay.kt` 대응.
struct PoseSkeletonOverlay: View {
    let frame: PoseLandmarkerHelper.PoseFrame?

    var body: some View {
        GeometryReader { geo in
            if let frame = frame {
                let w = geo.size.width
                let h = geo.size.height
                ZStack {
                    ForEach(Array(frame.allPoints.keys), id: \.self) { name in
                        if let p = frame.allPoints[name], p.confidence > 0.2 {
                            Circle()
                                .fill(.gabpaYellow)
                                .frame(width: 6, height: 6)
                                .position(x: CGFloat(p.x) * w, y: CGFloat(p.y) * h)
                        }
                    }
                }
            }
        }
        .allowsHitTesting(false)
    }
}
