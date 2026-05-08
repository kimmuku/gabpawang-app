import Foundation
import SwiftData

/// 로컬에 저장되는 운동 세션 1건. `WorkoutSessionEntity.kt`(Room) 포팅.
///
/// `@Model` 클래스는 `PersistentModel`이 자동으로 `Identifiable`을 부여하므로
/// SwiftUI의 `ForEach`에서 그대로 사용 가능하다.
@Model
final class WorkoutSessionEntity {
    var dateMillis: Int64
    var totalReps: Int
    var sets: Int
    var durationSec: Int
    /// 세트별 횟수를 콤마 구분 문자열로 저장 (Room 호환).
    var setHistory: String
    /// "free", "target", "challenge"
    var mode: String

    init(
        dateMillis: Int64 = Int64(Date().timeIntervalSince1970 * 1000),
        totalReps: Int,
        sets: Int,
        durationSec: Int,
        setHistory: String = "",
        mode: String = ""
    ) {
        self.dateMillis = dateMillis
        self.totalReps = totalReps
        self.sets = sets
        self.durationSec = durationSec
        self.setHistory = setHistory
        self.mode = mode
    }

    var date: Date { Date(timeIntervalSince1970: TimeInterval(dateMillis) / 1000) }

    /// 세트별 횟수 배열로 디코드. 없으면 totalReps 단일 값.
    var setHistoryArray: [Int] {
        let parts = setHistory
            .split(separator: ",")
            .compactMap { Int($0.trimmingCharacters(in: .whitespaces)) }
        return parts.isEmpty ? [totalReps] : parts
    }
}
