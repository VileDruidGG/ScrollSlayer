package com.example.scrollslayer.data.repository

import com.example.scrollslayer.data.local.ApiKeyStore
import com.example.scrollslayer.data.remote.*

/**
 * Repository for LearnPath AI resource search.
 * Handles provider resolution (BYOK vs free tier) transparently.
 */
class LearnPathRepository(
    private val api: LearnPathApi,
    private val keyStore: ApiKeyStore
) {
    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    }

    suspend fun findResources(
        goal: String,
        language: String = "es",
        pricing: String = "all",
        types: List<String> = emptyList()
    ): Result<GoalResponse> {
        return try {
            val activeProvider = keyStore.getActiveProvider()
            val apiKey = activeProvider?.let { keyStore.getKey(it) }

            val request = GoalRequest(
                goal = goal,
                language = language,
                pricing = pricing,
                resourceTypes = types.ifEmpty {
                    listOf("video", "podcast", "article", "documentation", "course")
                },
                provider = if (!apiKey.isNullOrBlank()) activeProvider else null,
                userApiKey = apiKey
            )

            val response = api.getResources(request)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    429 -> "Límite diario alcanzado. Conecta tu API key para uso ilimitado."
                    400 -> "Solicitud inválida. Verifica tu meta."
                    500 -> "Error del servidor. Intenta de nuevo."
                    else -> "Error (${response.code()})"
                }
                Result.Error(errorMsg, response.code())
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Sin conexión al servidor")
        }
    }

    suspend fun getProviders(): Result<ProvidersResponse> = try {
        val r = api.getProviders()
        if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
        else Result.Error("No se pudieron cargar los proveedores")
    } catch (e: Exception) {
        Result.Error(e.localizedMessage ?: "Error de conexión")
    }

    suspend fun validateKey(provider: String, apiKey: String): Result<ValidateKeyResponse> = try {
        val r = api.validateKey(ValidateKeyRequest(provider, apiKey))
        if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
        else Result.Error("Error al validar la key")
    } catch (e: Exception) {
        Result.Error(e.localizedMessage ?: "Error de conexión")
    }

    suspend fun getQuota(): Result<QuotaResponse> = try {
        val r = api.getQuota()
        if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
        else Result.Error("Error al consultar cuota")
    } catch (e: Exception) {
        Result.Error(e.localizedMessage ?: "Error de conexión")
    }
}
