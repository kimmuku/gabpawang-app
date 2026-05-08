import UIKit
import GoogleSignIn
import KakaoSDKCommon
import KakaoSDKAuth

/// Initializes the third-party SDKs (Kakao, Google) on launch.
///
/// `AuthRepository` handles the actual sign-in calls; this delegate only
/// performs the one-time SDK init that has to happen before any login.
final class AppDelegate: NSObject, UIApplicationDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        // Kakao SDK — replaces `KakaoSdk.init` from Android `GabpaApplication`.
        KakaoSDK.initSDK(appKey: AppConfig.kakaoAppKey)
        return true
    }

    /// Forwards URL callbacks for Google / Kakao OAuth.
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey : Any] = [:]
    ) -> Bool {
        if AuthApi.isKakaoTalkLoginUrl(url) {
            return AuthController.handleOpenUrl(url: url)
        }
        if GIDSignIn.sharedInstance.handle(url) {
            return true
        }
        return false
    }
}
