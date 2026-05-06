package com.gabpawang.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.feature.challenge.ChallengeScreen
import com.gabpawang.app.feature.character.CharacterScreen
import com.gabpawang.app.feature.home.HomeScreen
import com.gabpawang.app.feature.notifications.NotificationScreen
import com.gabpawang.app.feature.onboarding.OnboardingScreen
import com.gabpawang.app.feature.onboarding.SignupScreen
import com.gabpawang.app.feature.onboarding.SplashScreen
import com.gabpawang.app.feature.record.RecordScreen
import com.gabpawang.app.feature.settings.SettingsScreen
import com.gabpawang.app.feature.workout.LevelUpScreen
import com.gabpawang.app.feature.workout.WorkoutResultScreen
import com.gabpawang.app.feature.workout.WorkoutRunningScreen
import com.gabpawang.app.feature.workout.WorkoutStartScreen
import com.gabpawang.app.ui.theme.PushUpCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { PushUpCounterTheme { GabpaWangApp() } }
    }
}

@Composable
fun GabpaWangApp(
    appVm: AppViewModel = viewModel(),
    cameraVm: MainViewModel = viewModel()
) {
    val context = LocalContext.current
    val appState = remember { AppState() }
    val totalPushups by appVm.totalPushups.collectAsStateWithLifecycle()
    val charStage by appVm.charStage.collectAsStateWithLifecycle()
    val streak by appVm.streak.collectAsStateWithLifecycle()
    val oneRepMax by appVm.oneRepMax.collectAsStateWithLifecycle()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) launcher.launch(Manifest.permission.CAMERA)
    }

    // Home: let system handle back (exits the app).
    // Bottom-nav tabs (challenge/record/settings): back goes to home.
    // All other screens: pop the navigation stack.
    BackHandler(enabled = appState.screen != "home") {
        when (appState.screen) {
            "challenge", "record", "settings" -> appState.goRoot("home")
            else -> appState.back()
        }
    }

    // Permission gate is bypassed for non-camera screens.
    // We only require camera for the workout screen.
    if (appState.screen == "workout" && !hasPermission) {
        PermissionScreen { launcher.launch(Manifest.permission.CAMERA) }
        return
    }

    AppRouter(
        appState = appState,
        vm = cameraVm,
        appVm = appVm,
        totalPushups = totalPushups,
        charStage = charStage,
        streak = streak,
        oneRepMax = oneRepMax,
        context = context
    )
}

@Composable
private fun AppRouter(
    appState: AppState,
    vm: MainViewModel,
    appVm: AppViewModel,
    totalPushups: Int,
    charStage: Int,
    streak: Int,
    oneRepMax: Int,
    context: android.content.Context
) {
    when (appState.screen) {
        "splash" -> SplashScreen { appState.go("onboarding") }
        "onboarding" -> OnboardingScreen { appState.go("signup") }
        "signup" -> SignupScreen(
            onNext = { appState.goRoot("home") },
            onGoogleSignIn = {
                appVm.signInWithGoogle(
                    context = context,
                    onSuccess = { appState.goRoot("home") },
                    onError = { /* no-op: login failure is non-fatal */ }
                )
            },
            onKakaoSignIn = {
                appVm.signInWithKakao(
                    context = context,
                    onSuccess = { appState.goRoot("home") },
                    onError = { /* no-op: login failure is non-fatal */ }
                )
            }
        )
        "home" -> HomeScreen(
            charStage = charStage,
            totalPushups = totalPushups,
            streak = streak,
            oneRepMax = oneRepMax,
            onNav = { appState.goNav(it) },
            onStartWorkout = { appState.go("workoutStart") },
            onNotif = { appState.go("notifications") },
            onCharacter = { appState.go("character") },
            onStatClick = { tab ->
                appState.recordInitialTab = tab
                appState.go("record")
            }
        )
        "workoutStart" -> WorkoutStartScreen(
            onBack = { appState.back() },
            onStart = { cfg ->
                appState.workoutConfig = cfg
                appState.go("workout")
            }
        )
        "workout" -> WorkoutRunningScreen(
            config = appState.workoutConfig,
            vm = vm,
            voiceEnabled = appState.voiceEnabled,
            onFinish = { appState.onWorkoutFinish(it) }
        )
        "result" -> WorkoutResultScreen(
            result = appState.workoutResult ?: WorkoutResult(0, 0, emptyList()),
            charStage = charStage,
            totalPushups = totalPushups,
            onHome = { adjustedTotal ->
                val prevStage = charStage
                appState.workoutResult?.let { result ->
                    val finalResult = result.copy(total = adjustedTotal)
                    appVm.saveWorkout(finalResult)
                    val newTotal = totalPushups + adjustedTotal
                    val newStage = stageFor(newTotal)
                    if (newStage > prevStage) {
                        appState.levelUpStage = newStage
                        // Home is the base so back from levelup returns to home
                        appState.goRoot("home")
                        appState.go("levelup")
                    } else {
                        appState.goRoot("home")
                    }
                } ?: appState.goRoot("home")
            }
        )
        "levelup" -> LevelUpScreen(
            newStage = appState.levelUpStage,
            onNext = { appState.goRoot("home") }
        )
        "record" -> RecordScreen(
            initialTab = appState.recordInitialTab,
            onNav = { appState.goNav(it) }
        )
        "character" -> CharacterScreen(
            charStage = charStage,
            totalPushups = totalPushups,
            onBack = { appState.back() }
        )
        "challenge" -> ChallengeScreen(onNav = { appState.goNav(it) })
        "notifications" -> NotificationScreen(onBack = { appState.back() })
        "settings" -> SettingsScreen(
            onNav = { appState.goNav(it) },
            voiceEnabled = appState.voiceEnabled,
            onVoiceChange = { appState.voiceEnabled = it }
        )
        else -> SplashScreen { appState.go("onboarding") }
    }
}
