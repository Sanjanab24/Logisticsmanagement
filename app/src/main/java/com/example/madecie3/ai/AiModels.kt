package com.example.madecie3.ai

import com.google.gson.annotations.SerializedName

data class AiRequest(
    val model: String = "google/gemma-3n-e4b-it",
    val messages: List<AiMessage>,
    @SerializedName("max_tokens") val maxTokens: Int = 512,
    val temperature: Float = 0.20f,
    @SerializedName("top_p") val topP: Float = 0.70f,
    @SerializedName("frequency_penalty") val frequencyPenalty: Float = 0.00f,
    @SerializedName("presence_penalty") val presencePenalty: Float = 0.00f,
    val stream: Boolean = false
)

data class AiMessage(
    val role: String,
    val content: String
)

data class AiResponse(
    val choices: List<AiChoice>
)

data class AiChoice(
    val message: AiMessage
)
