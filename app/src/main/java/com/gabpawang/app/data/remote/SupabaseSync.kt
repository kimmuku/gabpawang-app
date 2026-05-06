package com.gabpawang.app.data.remote

import android.util.Log
import com.gabpawang.app.data.remote.dto.WorkoutSessionDto
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.signInAnonymously
import io.github.jan.supabase.postgrest.postgrest

private const val TAG = "SupabaseSync"
private const val TABLE = "workout_sessions"

/** Handles anonymous authentication and best-effort session upload to Supabase. */
object SupabaseSync {
    private val client get() = SupabaseClientProvider.client

    /** Ensures the user has an anonymous session. Call on app start. */
    suspend fun ensureAnonymousSession() {
        try {
            val session = client.auth.currentSessionOrNull()
            if (session == null) {
                client.auth.signInAnonymously()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Anonymous sign-in failed: ${e.message}")
        }
    }

    /** Uploads a single workout session to Supabase. Best-effort — does not throw. */
    suspend fun uploadSession(dto: WorkoutSessionDto) {
        try {
            client.postgrest[TABLE].insert(dto)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to upload session: ${e.message}")
        }
    }
}
