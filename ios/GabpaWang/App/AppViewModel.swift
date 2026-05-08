import Foundation
import Combine
import SwiftData

/// DB-backed 상태(누적/단계/연속/1RM)를 노출. `AppViewModel.kt` 포팅.
///
/// SwiftData가 KVO 없이 변경 알림을 잘 안 흘리기 때문에, 명시적으로
/// `refresh()`를 호출하는 흐름을 사용한다.
@MainActor
final class AppViewModel: ObservableObject {
    private let repository: WorkoutRepository

    @Published private(set) var totalPushups: Int = 0
    @Published private(set) var oneRepMax: Int = 0
    @Published private(set) var streak: Int = 0

    var charStage: Int { stageFor(total: totalPushups) }

    init(repository: WorkoutRepository) {
        self.repository = repository
        refresh()
    }

    func refresh() {
        totalPushups = repository.totalReps()
        oneRepMax = repository.oneRepMax()
        streak = repository.currentStreak()
    }

    /// 운동 종료 시 호출.
    func saveWorkout(_ result: WorkoutResult, mode: String = "") async {
        _ = await repository.saveSession(
            totalReps: result.total,
            sets: result.sets,
            durationSec: result.durationSec,
            history: result.history,
            mode: mode
        )
        refresh()
    }

    /// `RecordView` 등에서 직접 세션 목록을 조회할 때 사용.
    func allSessions() -> [WorkoutSessionEntity] {
        repository.fetchAllSessions()
    }
}
