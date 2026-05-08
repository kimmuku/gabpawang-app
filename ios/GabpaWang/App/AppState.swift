import SwiftUI
import Combine

/// Result of a finished workout session. Mirrors `WorkoutResult` data class.
struct WorkoutResult: Equatable {
    var sets: Int
    var total: Int
    var history: [Int]
    var durationSec: Int = 0
}

/// Configuration for the about-to-start workout. Mirrors `WorkoutConfig`.
struct WorkoutConfig: Equatable {
    var mode: String = "free" // "free", "target", "challenge", "timed"
    var targetCounts: [Int] = [30, 25, 20]
    var targetSets: Int = 3
    var timedSecs: Int = 120
}

/// Navigable screens. The original `AppState` used String routes; we keep
/// the same names so the mapping is one-to-one.
enum Screen: String, Hashable {
    case tutorial
    case home
    case workoutStart
    case workout
    case result
    case levelup
    case record
    case character
    case challenge
    case notifications
    case settings
}

/// Centralized navigation + transient UI state. DB-backed values live in
/// `AppViewModel`. This is the SwiftUI port of `AppState.kt`.
@MainActor
final class AppState: ObservableObject {
    @Published private(set) var backStack: [Screen]

    @Published var workoutResult: WorkoutResult?
    @Published var workoutConfig: WorkoutConfig = WorkoutConfig()
    @Published var levelUpStage: Int = 1
    @Published var voiceEnabled: Bool {
        didSet { UserDefaults.standard.set(voiceEnabled, forKey: Keys.voiceEnabled) }
    }
    @Published var isDarkTheme: Bool {
        didSet { UserDefaults.standard.set(isDarkTheme, forKey: Keys.darkTheme) }
    }

    private enum Keys {
        static let tutorialSeen = "tutorial_seen"
        static let voiceEnabled = "voice_enabled"
        static let darkTheme = "dark_theme"
    }

    var screen: Screen { backStack.last ?? .home }
    var canGoBack: Bool { backStack.count > 1 }

    init() {
        let defaults = UserDefaults.standard
        if defaults.object(forKey: Keys.darkTheme) == nil {
            defaults.set(true, forKey: Keys.darkTheme)
        }
        if defaults.object(forKey: Keys.voiceEnabled) == nil {
            defaults.set(true, forKey: Keys.voiceEnabled)
        }
        self.isDarkTheme = defaults.bool(forKey: Keys.darkTheme)
        self.voiceEnabled = defaults.bool(forKey: Keys.voiceEnabled)

        let tutorialSeen = defaults.bool(forKey: Keys.tutorialSeen)
        self.backStack = [tutorialSeen ? .home : .tutorial]
    }

    /// Push a new screen onto the back stack.
    func go(_ s: Screen) { backStack.append(s) }

    /// Clear the stack and set a single root screen (no back from here).
    func goRoot(_ s: Screen) { backStack = [s] }

    /// Pop the current screen; does nothing if already at root.
    func back() { if backStack.count > 1 { backStack.removeLast() } }

    /// Bottom-nav routes always reset the stack so back exits the app from any tab.
    func goNav(_ id: Screen) {
        switch id {
        case .home, .record, .settings: goRoot(id)
        default: break
        }
    }

    /// Called when a workout session completes. Stack is cleared so back
    /// cannot re-enter the camera.
    func onWorkoutFinish(_ result: WorkoutResult) {
        workoutResult = result
        goRoot(.result)
    }

    func markTutorialSeen() {
        UserDefaults.standard.set(true, forKey: Keys.tutorialSeen)
    }
}
