import Foundation
import SwiftData

/// 로컬 SwiftData + Supabase 동기화. `WorkoutRepository.kt` 포팅.
@MainActor
final class WorkoutRepository {
    private let context: ModelContext

    init(modelContext: ModelContext) {
        self.context = modelContext
    }

    // MARK: - Read

    func fetchAllSessions() -> [WorkoutSessionEntity] {
        let descriptor = FetchDescriptor<WorkoutSessionEntity>(
            sortBy: [SortDescriptor(\.dateMillis, order: .reverse)]
        )
        return (try? context.fetch(descriptor)) ?? []
    }

    func totalReps() -> Int {
        fetchAllSessions().reduce(0) { $0 + $1.totalReps }
    }

    /// 1회 최고기록 — 모든 세트 중 최댓값.
    func oneRepMax() -> Int {
        fetchAllSessions().flatMap { $0.setHistoryArray }.max() ?? 0
    }

    /// 운동한 distinct 일자 (yyyy-MM-dd, 내림차순).
    func distinctDays() -> [String] {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.locale = Locale.current
        formatter.timeZone = .current
        let days = Set(fetchAllSessions().map { formatter.string(from: $0.date) })
        return days.sorted(by: >)
    }

    /// 연속 운동일수. `streakFlow()` 포팅.
    func currentStreak() -> Int {
        let days = distinctDays()
        guard !days.isEmpty else { return 0 }
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.locale = Locale.current

        var streak = 0
        var cursor = Date()
        let cal = Calendar.current

        for (i, day) in days.enumerated() {
            let expected = formatter.string(from: cursor)
            if day == expected {
                streak += 1
                cursor = cal.date(byAdding: .day, value: -1, to: cursor) ?? cursor
            } else if i == 0 {
                cursor = cal.date(byAdding: .day, value: -1, to: cursor) ?? cursor
                if day == formatter.string(from: cursor) {
                    streak += 1
                    cursor = cal.date(byAdding: .day, value: -1, to: cursor) ?? cursor
                } else {
                    break
                }
            } else {
                break
            }
        }
        return streak
    }

    // MARK: - Write

    @discardableResult
    func saveSession(
        totalReps: Int,
        sets: Int,
        durationSec: Int,
        history: [Int] = [],
        mode: String = ""
    ) async -> WorkoutSessionEntity {
        let dateMillis = Int64(Date().timeIntervalSince1970 * 1000)
        let entity = WorkoutSessionEntity(
            dateMillis: dateMillis,
            totalReps: totalReps,
            sets: sets,
            durationSec: durationSec,
            setHistory: history.map(String.init).joined(separator: ","),
            mode: mode
        )
        context.insert(entity)
        try? context.save()

        // 베스트 에포트 클라우드 백업.
        await SupabaseSync.shared.uploadSession(
            WorkoutSessionDTO(
                dateMillis: dateMillis,
                totalReps: totalReps,
                sets: sets,
                durationSec: durationSec
            )
        )
        return entity
    }
}
