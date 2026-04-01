package com.example.scrollslayer.data.remote

import retrofit2.Response
import retrofit2.http.*

interface LearnPathApi {

    @POST("api/v1/resources")
    suspend fun getResources(@Body request: GoalRequest): Response<GoalResponse>

    @GET("api/v1/providers")
    suspend fun getProviders(): Response<ProvidersResponse>

    @POST("api/v1/validate-key")
    suspend fun validateKey(@Body request: ValidateKeyRequest): Response<ValidateKeyResponse>

    @GET("api/v1/quota")
    suspend fun getQuota(): Response<QuotaResponse>

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, Any>>
}
