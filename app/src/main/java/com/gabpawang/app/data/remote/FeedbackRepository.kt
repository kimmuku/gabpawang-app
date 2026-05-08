package com.gabpawang.app.data.remote

import com.gabpawang.app.data.remote.dto.FeedbackDto
import io.github.jan.supabase.postgrest.postgrest

object FeedbackRepository {
    suspend fun submit(content: String, appVersion: String, deviceModel: String): Result<Unit> =
        runCatching {
            val dto = FeedbackDto(content = content, appVersion = appVersion, deviceModel = deviceModel)
            SupabaseClientProvider.client.postgrest["feedback"].insert(dto)
            Unit
        }
}
