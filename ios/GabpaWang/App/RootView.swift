import SwiftUI

/// 라우터 — `MainActivity.kt`의 `AppRouter` 포팅.
struct RootView: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var appVM: AppViewModel

    var body: some View {
        let colors = appState.isDarkTheme ? AppColors.dark : AppColors.light

        ZStack {
            colors.bgDark.ignoresSafeArea()

            switch appState.screen {
            case .tutorial:
                TutorialView(onStart: {
                    appState.markTutorialSeen()
                    appState.goRoot(.home)
                })

            case .home:
                HomeView(
                    charStage: appVM.charStage,
                    totalPushups: appVM.totalPushups,
                    oneRepMax: appVM.oneRepMax,
                    onNav: { appState.goNav($0) },
                    onStartWorkout: { appState.go(.workoutStart) },
                    onCharacter: { appState.go(.character) },
                    onNotifications: { appState.go(.notifications) }
                )

            case .workoutStart:
                WorkoutStartView(
                    onBack: { appState.back() },
                    onStart: { cfg in
                        appState.workoutConfig = cfg
                        appState.go(.workout)
                    }
                )

            case .workout:
                WorkoutRunningView(
                    config: appState.workoutConfig,
                    voiceEnabled: appState.voiceEnabled,
                    onFinish: { appState.onWorkoutFinish($0) }
                )

            case .result:
                WorkoutResultView(
                    result: appState.workoutResult ?? WorkoutResult(sets: 0, total: 0, history: []),
                    charStage: appVM.charStage,
                    totalPushups: appVM.totalPushups,
                    onHome: { adjustedTotal, adjustedHistory in
                        let prevStage = appVM.charStage
                        if let result = appState.workoutResult {
                            let final = WorkoutResult(
                                sets: result.sets,
                                total: adjustedTotal,
                                history: adjustedHistory,
                                durationSec: result.durationSec
                            )
                            Task {
                                await appVM.saveWorkout(final, mode: appState.workoutConfig.mode)
                            }
                            let newTotal = appVM.totalPushups + adjustedTotal
                            let newStage = stageFor(total: newTotal)
                            if newStage > prevStage {
                                appState.levelUpStage = newStage
                                appState.goRoot(.home)
                                appState.go(.levelup)
                            } else {
                                appState.goRoot(.home)
                            }
                        } else {
                            appState.goRoot(.home)
                        }
                    }
                )

            case .levelup:
                LevelUpView(
                    newStage: appState.levelUpStage,
                    onNext: { appState.goRoot(.home) }
                )

            case .record:
                RecordView(onNav: { appState.goNav($0) })

            case .character:
                CharacterView(
                    charStage: appVM.charStage,
                    totalPushups: appVM.totalPushups,
                    onBack: { appState.back() }
                )

            case .notifications:
                NotificationsView(onBack: { appState.back() })

            case .challenge:
                ChallengeView(onBack: { appState.back() })

            case .settings:
                SettingsView(onNav: { appState.goNav($0) })
            }
        }
        .environment(\.appColors, colors)
        .preferredColorScheme(appState.isDarkTheme ? .dark : .light)
    }
}
