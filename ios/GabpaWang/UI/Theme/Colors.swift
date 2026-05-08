import SwiftUI

// MARK: - Brand accents (theme-invariant)

extension Color {
    static let gabpaYellow      = Color(red: 1.00, green: 0.769, blue: 0.000) // #FFC400
    static let gabpaOrange      = Color(red: 1.00, green: 0.549, blue: 0.000) // #FF8C00
    static let gabpaRed         = Color(red: 1.00, green: 0.333, blue: 0.000) // #FF5500
    static let gabpaGold        = Color(red: 1.00, green: 0.867, blue: 0.000) // #FFDD00
    static let gabpaGreenAccent = Color(red: 0.00, green: 0.784, blue: 0.325) // #00C853
    static let gabpaBlueAccent  = Color(red: 0.00, green: 0.765, blue: 1.000) // #00C3FF
    static let gabpaRedAlert    = Color(red: 1.00, green: 0.376, blue: 0.376) // #FF6060
    static let kakaoYellow      = Color(red: 1.00, green: 0.898, blue: 0.000) // #FEE500
    static let kakaoText        = Color(red: 0.235, green: 0.118, blue: 0.118) // #3C1E1E
    static let naverGreen       = Color(red: 0.012, green: 0.780, blue: 0.353) // #03C75A
}

// MARK: - Theme-aware semantic colors

/// Semantic color set, port of Android `AppColors`. Drives backgrounds,
/// text, and borders for both dark (default) and light themes.
struct AppColors: Equatable {
    let bgDark: Color
    let bgSheet: Color
    let bgCard: Color
    let textPrimary: Color
    let textSub: Color
    let borderCard: Color
    let accent: Color
}

extension AppColors {
    static let dark = AppColors(
        bgDark:       Color(red: 0.039, green: 0.039, blue: 0.071),  // #0A0A12
        bgSheet:      Color(red: 0.102, green: 0.102, blue: 0.149),  // #1A1A26
        bgCard:       Color.white.opacity(0.05),
        textPrimary:  Color.white,
        textSub:      Color.white.opacity(0.4),
        borderCard:   Color.white.opacity(0.08),
        accent:       .gabpaYellow
    )

    static let light = AppColors(
        bgDark:       Color(red: 0.941, green: 0.949, blue: 0.961),  // #F0F2F5
        bgSheet:      Color.white,
        bgCard:       Color.black.opacity(0.04),
        textPrimary:  Color(red: 0.102, green: 0.102, blue: 0.180),  // #1A1A2E
        textSub:      Color.black.opacity(0.5),
        borderCard:   Color.black.opacity(0.10),
        accent:       Color(red: 0.706, green: 0.325, blue: 0.035)   // #B45309
    )
}

// MARK: - EnvironmentKey — port of LocalAppColors composition local

private struct AppColorsKey: EnvironmentKey {
    static let defaultValue: AppColors = .dark
}

extension EnvironmentValues {
    var appColors: AppColors {
        get { self[AppColorsKey.self] }
        set { self[AppColorsKey.self] = newValue }
    }
}
