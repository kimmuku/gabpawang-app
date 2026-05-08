package com.gabpawang.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDto(
    val content: String,
    @SerialName("app_version") val appVersion: String,
    @SerialName("device_model") val deviceModel: String
)
