package se.ox.assigment.sdk.api


import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import se.ox.assigment.sdk.SdkConfig
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun createApiService(config: SdkConfig): ApiService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
            .writeTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}