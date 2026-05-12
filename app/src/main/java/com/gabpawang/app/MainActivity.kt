package com.gabpawang.app

import android.Manifest
import android.app.Activity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabpawang.app.billing.BillingManager
import com.gabpawang.app.feature.character.CharacterScreen
import com.gabpawang.app.feature.home.HomeScreen
import com.gabpawang.app.feature.notifications.NotificationScreen
import com.gabpawang.app.feature.onboarding.TutorialScreen
import com.gabpawang.app.feature.record.RecordScreen
import com.gabpawang.app.feature.settings.FeedbackScreen
import com.gabpawang.app.feature.settings.SettingsScreen
import com.gabpawang.app.feature.workout.LevelUpScreen
import com.gabpawang.app.feature.workout.WorkoutResultScreen
import com.gabpawang.app.feature.workout.WorkoutRunningScreen
import com.gabpawang.app.feature.workout.WorkoutStartScreen
import com.gabpawang.app.ui.theme.DarkAppColors
import com.gabpawang.app.ui.theme.LightAppColors
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.PushUpCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
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
    // Check if the user has already seen the tutorial; first-time users start on "tutorial".
    val tutorialSeen = remember {
        context.getSharedPreferences("gabpa_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("tutorial_seen", false)
    }
    val savedDarkTheme = remember {
        context.getSharedPreferences("gabpa_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("dark_theme", true)
    }
    val appState = remember {
        AppState(if (tutorialSeen) "home" else "tutorial").also { it.isDarkTheme = savedDarkTheme }
    }
    val totalPushups by appVm.totalPushups.collectAsStateWithLifecycle()
    val charStage by appVm.charStage.collectAsStateWithLifecycle()
    val oneRepMax by appVm.oneRepMax.collectAsStateWithLifecycle()
    val streak by appVm.streak.collectAsStateWithLifecycle()
    val isAdFree by BillingManager.isAdFree.collectAsStateWithLifecycle()
    val adPrice by BillingManager.price.collectAsStateWithLifecycle()

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

    // Re-provide LocalAppColors based on current theme preference so all children react to changes.
    val appColors = if (appState.isDarkTheme) DarkAppColors else LightAppColors

    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !appState.isDarkTheme
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        // Permission gate is bypassed for non-camera screens.
        // We only require camera for the workout screen.
        if (appState.screen == "workout" && !hasPermission) {
            PermissionScreen { launcher.launch(Manifest.permission.CAMERA) }
            return@CompositionLocalProvider
        }

        AppRouter(
            appState = appState,
            vm = cameraVm,
            appVm = appVm,
            totalPushups = totalPushups,
            charStage = charStage,
            oneRepMax = oneRepMax,
            streak = streak,
            isAdFree = isAdFree,
            adPrice = adPrice,
            context = context
        )
    }
}

@Composable
private fun AppRouter(
    appState: AppState,
    vm: MainViewModel,
    appVm: AppViewModel,
    totalPushups: Int,
    charStage: Int,
    oneRepMax: Int,
    streak: Int,
    isAdFree: Boolean,
    adPrice: String,
    context: android.content.Context
) {
    when (appState.screen) {
        "tutorial" -> TutorialScreen(
            onStart = {
                // Mark tutorial as seen so it won't show again on next launch.
                context.getSharedPreferences("gabpa_prefs", android.content.Context.MODE_PRIVATE)
                    .edit().putBoolean("tutorial_seen", true).apply()
                appState.goRoot("home")
            }
        )
        "home" -> HomeScreen(
            charStage = charStage,
            totalPushups = totalPushups,
            oneRepMax = oneRepMax,
            streak = streak,
            onNav = { appState.goNav(it) },
            onStartWorkout = { appState.go("workoutStart") },
            onCharacter = { appState.go("character") }
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
            musicEnabled = appState.musicEnabled,
            totalPushups = totalPushups,
            onFinish = { appState.onWorkoutFinish(it) }
        )
        "result" -> WorkoutResultScreen(
            result = appState.workoutResult ?: WorkoutResult(0, 0, emptyList()),
            charStage = charStage,
            totalPushups = totalPushups,
            onSave = { adjustedTotal, adjustedHistory ->
                val prevStage = charStage
                appState.workoutResult?.let { result ->
                    val finalResult = result.copy(total = adjustedTotal, history = adjustedHistory)
                    appVm.saveWorkout(finalResult, mode = appState.workoutConfig.mode)
                    val newTotal = totalPushups + adjustedTotal
                    val newStage = stageFor(newTotal)
                    if (newStage > prevStage) {
                        appState.levelUpStage = newStage
                        appState.goRoot("home")
                        appState.go("levelup")
                    } else {
                        appState.goRoot("home")
                    }
                } ?: appState.goRoot("home")
            },
            onHome = { appState.goRoot("home") }
        )
        "levelup" -> LevelUpScreen(
            newStage = appState.levelUpStage,
            onNext = { appState.goRoot("home") }
        )
        "record" -> RecordScreen(onNav = { appState.goNav(it) })
        "character" -> CharacterScreen(
            charStage = charStage,
            totalPushups = totalPushups,
            onBack = { appState.back() }
        )
        "notifications" -> NotificationScreen(onBack = { appState.back() })
        "settings" -> SettingsScreen(
            onNav = { appState.goNav(it) },
            onFeedback = { appState.go("feedback") },
            voiceEnabled = appState.voiceEnabled,
            onVoiceChange = { appState.voiceEnabled = it },
            musicEnabled = appState.musicEnabled,
            onMusicChange = { appState.musicEnabled = it },
            isDarkTheme = appState.isDarkTheme,
            onThemeChange = { dark ->
                appState.isDarkTheme = dark
                context.getSharedPreferences("gabpa_prefs", android.content.Context.MODE_PRIVATE)
                    .edit().putBoolean("dark_theme", dark).apply()
            },
            isAdFree = isAdFree,
            adRemovalPrice = adPrice,
            onRemoveAds = {
                (context as? Activity)?.let { BillingManager.launchPurchase(it) }
            }
        )
        "feedback" -> FeedbackScreen(onBack = { appState.back() })
    }
}
