package com.gabpawang.app

import android.app.Application
import androidx.room.Room
import com.gabpawang.app.data.WorkoutRepository
import com.gabpawang.app.data.db.GabpaDatabase
import com.gabpawang.app.data.remote.AuthRepository
import com.gabpawang.app.data.remote.SupabaseSync
import com.kakao.sdk.common.KakaoSdk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** Application class that initializes Room, WorkoutRepository, AuthRepository, and SDKs. */
class GabpaApplication : Application() {
    val database: GabpaDatabase by lazy {
        Room.databaseBuilder(this, GabpaDatabase::class.java, "gabpa.db").build()
    }
    val repository: WorkoutRepository by lazy { WorkoutRepository(database) }
    val authRepository: AuthRepository by lazy { AuthRepository(this) }

    override fun onCreate() {
        super.onCreate()
        // Initialize Kakao SDK before any login calls.
        KakaoSdk.init(this, "73af7802f9ee24065925157c33d9e031")
        // Start anonymous Supabase session in background (tied to Application lifecycle).
        @Suppress("OPT_IN_USAGE")
        GlobalScope.launch {
            SupabaseSync.ensureAnonymousSession()
        }
    }
}
