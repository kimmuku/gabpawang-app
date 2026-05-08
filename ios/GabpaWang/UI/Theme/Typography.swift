import SwiftUI

/// 타이포그래피 — Pretendard 기반. `Type.kt`의 매핑을 그대로 옮김.
/// 폰트 파일은 `Resources/Fonts/`에 추가하고 Info.plist의 `UIAppFonts`에 등록해야 함.
enum GabpaTypography {
    static let display = Font.custom("Pretendard-ExtraBold", size: 32)
    static let titleXL  = Font.custom("Pretendard-ExtraBold", size: 24)
    static let titleL   = Font.custom("Pretendard-ExtraBold", size: 20)
    static let titleM   = Font.custom("Pretendard-Bold", size: 18)
    static let titleS   = Font.custom("Pretendard-Bold", size: 16)
    static let bodyL    = Font.custom("Pretendard-Regular", size: 16)
    static let bodyM    = Font.custom("Pretendard-Regular", size: 14)
    static let bodyS    = Font.custom("Pretendard-Regular", size: 12)
    static let labelL   = Font.custom("Pretendard-Medium", size: 14)
    static let labelM   = Font.custom("Pretendard-Medium", size: 12)
    static let labelS   = Font.custom("Pretendard-Regular", size: 11)
    static let buttonL  = Font.custom("Pretendard-Bold", size: 17)
    static let stat     = Font.custom("Pretendard-ExtraBold", size: 22)
}

extension Font {
    static var gabpaDefault: Font { GabpaTypography.bodyL }
}
