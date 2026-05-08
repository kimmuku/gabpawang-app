import SwiftUI
import SwiftData

/// 앱 진입점. `MainActivity.kt` + `GabpaApplication.kt`에 대응.
@main
struct GabpaWangApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

    /// SwiftData 컨테이너 — Room DB 대체.
    let modelContainer: ModelContainer

    @StateObject private var appState: AppState
    @StateObject private var appViewModel: AppViewModel

    init() {
        do {
            let container = try ModelContainer(for: WorkoutSessionEntity.self)
            self.modelContainer = container
            let repo = WorkoutRepository(modelContext: container.mainContext)
            _appState = StateObject(wrappedValue: AppState())
            _appViewModel = StateObject(wrappedValue: AppViewModel(repository: repo))
        } catch {
            fatalError("Failed to create ModelContainer: \(error)")
        }
        // 카메라 흐름 중 화면이 어두워지지 않도록.
        UIApplication.shared.isIdleTimerDisabled = true
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(appState)
                .environmentObject(appViewModel)
                .preferredColorScheme(appState.isDarkTheme ? .dark : .light)
                .onOpenURL { url in
                    AuthRepository.shared.handleOpenURL(url)
                }
                .task {
                    await SupabaseSync.shared.ensureAnonymousSession()
                }
        }
        .modelContainer(modelContainer)
    }
}
