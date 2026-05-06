package com.gabpawang.app.data.remote

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.kakao.sdk.user.UserApiClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "AuthRepository"
private const val GOOGLE_CLIENT_ID =
    "680384045148-vlc7ffln8lou838mh0ka4cd3ce2cgceh.apps.googleusercontent.com"

/** Handles social sign-in flows (Google via CredentialManager, Kakao via SDK). */
class AuthRepository(private val context: Context) {

    private val supabase get() = SupabaseClientProvider.client

    /**
     * Signs in with Google using CredentialManager and forwards the ID token to Supabase Auth.
     * Requires Activity context for the credential picker UI.
     */
    suspend fun signInWithGoogle(): Result<Unit> = runCatching {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(GOOGLE_CLIENT_ID)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val result = credentialManager.getCredential(context, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
        supabase.auth.signInWith(IDToken) {
            idToken = googleCredential.idToken
            provider = Google
        }
        Log.d(TAG, "Google sign-in succeeded")
    }

    /**
     * Signs in with Kakao using KakaoTalk app if available, otherwise web login.
     * Kakao session is kept locally; Supabase anonymous session remains unchanged.
     */
    suspend fun signInWithKakao(): Result<Unit> = runCatching {
        suspendCancellableCoroutine { cont ->
            val callback: (com.kakao.sdk.auth.model.OAuthToken?, Throwable?) -> Unit =
                { token, error ->
                    when {
                        error != null -> {
                            Log.w(TAG, "Kakao login failed", error)
                            cont.resumeWithException(error)
                        }
                        token != null -> {
                            Log.d(TAG, "Kakao login succeeded")
                            cont.resume(Unit)
                        }
                        else -> cont.resumeWithException(IllegalStateException("Kakao: no token and no error"))
                    }
                }
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            }
        }
    }
}
