import Foundation

/// Static configuration. Values mirror the Android `SupabaseConfig.kt` and
/// `AuthRepository.kt` constants. Replace before shipping.
enum AppConfig {
    static let supabaseURL = URL(
        string: "https://ypamuhthdsgzsdlitkkc.supabase.co"
    )!
    static let supabaseAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlwYW11aHRoZHNnenNkbGl0a2tjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc5NDcxNTQsImV4cCI6MjA5MzUyMzE1NH0.6d7IA79o0FNK5lB8uf90R1_bxqhd1eYu3oGTtvr8X5o"

    /// Kakao native app key. Set in Kakao Developers Console > 앱 > 일반 > 앱 키 > 네이티브 앱 키.
    /// URL scheme `kakao{appKey}` must be added to Info.plist.
    static let kakaoAppKey = "73af7802f9ee24065925157c33d9e031"

    /// Google iOS client ID (Sign-In with Google). Add the reverse client ID as
    /// a URL scheme in Info.plist.
    static let googleClientID =
        "680384045148-vlc7ffln8lou838mh0ka4cd3ce2cgceh.apps.googleusercontent.com"
}
