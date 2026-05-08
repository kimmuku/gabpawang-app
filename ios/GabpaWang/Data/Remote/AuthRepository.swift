import Foundation
import UIKit
import Supabase
import GoogleSignIn
import KakaoSDKAuth
import KakaoSDKUser

/// 소셜 로그인. `AuthRepository.kt` 포팅.
///
/// - Google: GoogleSignIn-iOS → ID 토큰 → Supabase Auth
/// - Kakao: 카카오 SDK → 토큰 (Supabase는 카카오 직접 미지원이라
///   Edge Function `kakao-auth`로 교환하거나 별도 처리 필요)
@MainActor
final class AuthRepository {
    static let shared = AuthRepository()
    private init() {}

    private var supabase: SupabaseClient { SupabaseClientProvider.client }

    /// AppDelegate에서 호출되는 OAuth 콜백 핸들러.
    func handleOpenURL(_ url: URL) {
        if AuthApi.isKakaoTalkLoginUrl(url) {
            _ = AuthController.handleOpenUrl(url: url)
            return
        }
        _ = GIDSignIn.sharedInstance.handle(url)
    }

    // MARK: - Google

    /// 구글 로그인 후 Supabase에 ID 토큰 전달.
    func signInWithGoogle(presenter: UIViewController) async throws {
        let config = GIDConfiguration(clientID: AppConfig.googleClientID)
        GIDSignIn.sharedInstance.configuration = config

        let result = try await GIDSignIn.sharedInstance.signIn(withPresenting: presenter)
        guard let idToken = result.user.idToken?.tokenString else {
            throw NSError(domain: "AuthRepository", code: 1,
                          userInfo: [NSLocalizedDescriptionKey: "Google ID token missing"])
        }
        let accessToken = result.user.accessToken.tokenString
        try await supabase.auth.signInWithIdToken(
            credentials: .init(provider: .google, idToken: idToken, accessToken: accessToken)
        )
    }

    // MARK: - Kakao

    /// 카카오 로그인. KakaoTalk 앱이 있으면 그쪽을, 없으면 카카오 계정 웹 로그인을 사용.
    /// 받은 access_token은 Supabase Edge Function `kakao-auth`로 보내 세션을 시작한다.
    func signInWithKakao() async throws {
        let token = try await withCheckedThrowingContinuation { (cont: CheckedContinuation<OAuthToken, Error>) in
            let callback: (OAuthToken?, Error?) -> Void = { token, error in
                if let error = error { cont.resume(throwing: error); return }
                if let token = token { cont.resume(returning: token); return }
                cont.resume(throwing: NSError(
                    domain: "AuthRepository", code: 2,
                    userInfo: [NSLocalizedDescriptionKey: "Kakao: no token and no error"]
                ))
            }
            if UserApi.isKakaoTalkLoginAvailable() {
                UserApi.shared.loginWithKakaoTalk(completion: callback)
            } else {
                UserApi.shared.loginWithKakaoAccount(completion: callback)
            }
        }

        // Edge Function 호출 → 세션 토큰 받기 → verifyOTP로 Supabase 로그인.
        let endpoint = AppConfig.supabaseURL.appendingPathComponent("functions/v1/kakao-auth")
        var req = URLRequest(url: endpoint)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.setValue("Bearer \(AppConfig.supabaseAnonKey)", forHTTPHeaderField: "Authorization")
        req.httpBody = try JSONEncoder().encode([
            "access_token": token.accessToken
        ])
        let (data, _) = try await URLSession.shared.data(for: req)
        struct EdgeResponse: Decodable {
            let email: String
            let token_hash: String
        }
        let decoded = try JSONDecoder().decode(EdgeResponse.self, from: data)

        try await supabase.auth.verifyOTP(
            email: decoded.email,
            token: decoded.token_hash,
            type: .magiclink
        )
    }

    /// 로그아웃. Supabase + 두 SDK 모두 처리.
    func signOut() async {
        try? await supabase.auth.signOut()
        GIDSignIn.sharedInstance.signOut()
        UserApi.shared.logout { _ in }
    }
}
