package com.example.madecie3.ai

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApi {
    @POST("chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") auth: String,
        @Body request: AiRequest
    ): Response<AiResponse>
}

object AiClient {
    private const val BASE_URL = "https://integrate.api.nvidia.com/v1/"
    private const val API_KEY = "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"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val api: AiApi = retrofit.create(AiApi::class.java)

    fun getAuthHeader() = "Bearer $API_KEY"
}
