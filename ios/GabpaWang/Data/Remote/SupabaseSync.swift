import Foundation
import Supabase

/// 익명 로그인 + 세션 베스트 에포트 업로드. `SupabaseSync.kt` 포팅.
final class SupabaseSync {
    static let shared = SupabaseSync()
    private init() {}

    private let table = "workout_sessions"
    private var client: SupabaseClient { SupabaseClientProvider.client }

    /// 앱 시작 시 호출. 세션이 없으면 익명으로 만든다.
    func ensureAnonymousSession() async {
        do {
            // currentSession은 Supabase 2.x에서 throws로 가져옴.
            _ = try await client.auth.session
        } catch {
            do {
                _ = try await client.auth.signInAnonymously()
            } catch {
                // 네트워크 실패 등은 무시.
                #if DEBUG
                print("[SupabaseSync] anonymous sign-in failed: \(error.localizedDescription)")
                #endif
            }
        }
    }

    /// 운동 세션 1건 업로드. 실패 시 무시(베스트 에포트).
    func uploadSession(_ dto: WorkoutSessionDTO) async {
        do {
            try await client
                .from(table)
                .insert(dto)
                .execute()
        } catch {
            #if DEBUG
            print("[SupabaseSync] upload failed: \(error.localizedDescription)")
            #endif
        }
    }
}
