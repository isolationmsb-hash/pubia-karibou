package com.karibou.pubia.data.remote

import com.karibou.pubia.data.remote.dto.GenerateAdResponse
import com.karibou.pubia.data.remote.dto.HealthResponse
import com.karibou.pubia.data.remote.dto.ImproveScriptRequest
import com.karibou.pubia.data.remote.dto.ImproveScriptResponse
import com.karibou.pubia.data.remote.dto.JobStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Interface Retrofit du backend PubIA Karibou.
 * Toutes les routes (sauf /health) sont authentifiees via l'AuthInterceptor
 * qui ajoute le header X-API-Key.
 */
interface BackendApi {

    @GET("health")
    suspend fun health(): HealthResponse

    @Multipart
    @POST("api/generate-ad")
    suspend fun generateAd(
        @Part avatar: MultipartBody.Part,
        @Part product: MultipartBody.Part,
        @Part("script") script: RequestBody,
        @Part("durationSeconds") durationSeconds: RequestBody,
        @Part("format") format: RequestBody,
        @Part("voiceId") voiceId: RequestBody?
    ): GenerateAdResponse

    @GET("api/status/{jobId}")
    suspend fun getStatus(@Path("jobId") jobId: String): JobStatusResponse

    @POST("api/script/improve")
    suspend fun improveScript(@Body request: ImproveScriptRequest): ImproveScriptResponse
}
