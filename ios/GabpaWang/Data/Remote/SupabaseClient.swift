import Foundation
import Supabase

/// 싱글톤 Supabase 클라이언트. `SupabaseClientProvider.kt` 포팅.
enum SupabaseClientProvider {
    static let client: SupabaseClient = {
        SupabaseClient(
            supabaseURL: AppConfig.supabaseURL,
            supabaseKey: AppConfig.supabaseAnonKey
        )
    }()
}

/// Postgrest로 업로드되는 세션 DTO. `WorkoutSessionDto.kt` 포팅 — JSON 키
/// snake_case는 `Encodable.encode(to:)`로 명시적으로 매핑.
struct WorkoutSessionDTO: Codable {
    let dateMillis: Int64
    let totalReps: Int
    let sets: Int
    let durationSec: Int

    enum CodingKeys: String, CodingKey {
        case dateMillis = "date_millis"
        case totalReps = "total_reps"
        case sets
        case durationSec = "duration_sec"
    }
}
