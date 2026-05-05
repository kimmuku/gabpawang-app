package com.example.pushupcounter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pushupcounter.feature.challenge.ChallengeScreen
import com.example.pushupcounter.feature.character.CharacterScreen
import com.example.pushupcounter.feature.home.HomeScreen
import com.example.pushupcounter.feature.notifications.NotificationScreen
import com.example.pushupcounter.feature.onboarding.OnboardingScreen
import com.example.pushupcounter.feature.onboarding.SignupScreen
import com.example.pushupcounter.feature.onboarding.SplashScreen
import com.example.pushupcounter.feature.record.RecordScreen
import com.example.pushupcounter.feature.settings.SettingsScreen
import com.example.pushupcounter.feature.workout.LevelUpScreen
import com.example.pushupcounter.feature.workout.WorkoutResultScreen
import com.example.pushupcounter.feature.workout.WorkoutRunningScreen
import com.example.pushupcounter.feature.workout.WorkoutStartScreen
import com.example.pushupcounter.ui.theme.PushUpCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { PushUpCounterTheme { GabpaWangApp() } }
    }
}

@Composable
fun GabpaWangApp(vm: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val appState = remember { AppState() }

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

    // Permission gate is bypassed for non-camera screens.
    // We only require camera for the workout screen.
    if (appState.screen == "workout" && !hasPermission) {
        PermissionScreen { launcher.launch(Manifest.permission.CAMERA) }
        return
    }

    AppRouter(appState = appState, vm = vm)
}

@Composable
private fun AppRouter(appState: AppState, vm: MainViewModel) {
    when (appState.screen) {
        "splash" -> SplashScreen { appState.go("onboarding") }
        "onboarding" -> OnboardingScreen { appState.go("signup") }
        "signup" -> SignupScreen { appState.go("home") }
        "home" -> HomeScreen(
            charStage = appState.charStage,
            totalPushups = appState.totalPushups,
            streak = appState.streak,
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
            onBack = { appState.go("home") },
            onStart = { cfg ->
                appState.workoutConfig = cfg
                appState.go("workout")
            }
        )
        "workout" -> WorkoutRunningScreen(
            config = appState.workoutConfig,
            vm = vm,
            onFinish = { appState.onWorkoutFinish(it) }
        )
        "result" -> WorkoutResultScreen(
            result = appState.workoutResult ?: WorkoutResult(0, 0, emptyList()),
            charStage = appState.charStage,
            totalPushups = appState.totalPushups,
            onHome = { appState.onResultConfirm() }
        )
        "levelup" -> LevelUpScreen(
            newStage = appState.charStage,
            onNext = { appState.go("home") }
        )
        "record" -> RecordScreen(
            initialTab = appState.recordInitialTab,
            onNav = { appState.goNav(it) }
        )
        "character" -> CharacterScreen(
            charStage = appState.charStage,
            totalPushups = appState.totalPushups,
            onBack = { appState.go("home") }
        )
        "challenge" -> ChallengeScreen(onNav = { appState.goNav(it) })
        "notifications" -> NotificationScreen(onBack = { appState.go("home") })
        "settings" -> SettingsScreen(onNav = { appState.goNav(it) })
        else -> SplashScreen { appState.go("onboarding") }
    }
}
