package com.example.scrollslayer.data.remote

import com.google.gson.annotations.SerializedName

// ═══════════════════════════════════════════════════════════
// REQUEST
// ═══════════════════════════════════════════════════════════

data class GoalRequest(
    @SerializedName("goal") val goal: String,
    @SerializedName("language") val language: String = "es",
    @SerializedName("pricing") val pricing: String = "all",
    @SerializedName("resourceTypes") val resourceTypes: List<String> = listOf(
        "video", "podcast", "article", "documentation", "course"
    ),
    @SerializedName("limit") val limit: Int = 10,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("userApiKey") val userApiKey: String? = null
)

// ═══════════════════════════════════════════════════════════
// RESPONSE
// ═══════════════════════════════════════════════════════════

data class GoalResponse(
    @SerializedName("goal") val goal: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("roadmap") val roadmap: List<String>,
    @SerializedName("resources") val resources: List<LearningResource>,
    @SerializedName("filters") val filters: AppliedFilters,
    @SerializedName("meta") val meta: ResponseMeta
)

data class LearningResource(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("url") val url: String,
    @SerializedName("type") val type: String,
    @SerializedName("platform") val platform: String,
    @SerializedName("language") val language: String,
    @SerializedName("pricing") val pricing: String,
    @SerializedName("estimatedTime") val estimatedTime: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("tags") val tags: List<String>
) {
    val icon get() = when (type) {
        "video" -> "\uD83C\uDFAC"; "podcast" -> "\uD83C\uDF99\uFE0F"; "article" -> "\uD83D\uDCDD"
        "documentation" -> "\uD83D\uDCDA"; "course" -> "\uD83C\uDF93"; else -> "\uD83D\uDCCC"
    }
    val pricingLabel get() = when (pricing) {
        "free" -> "Gratis"; "paid" -> "De pago"
        "freemium" -> "Freemium"; else -> pricing
    }
    val difficultyLabel get() = when (difficulty) {
        "beginner" -> "Principiante"; "intermediate" -> "Intermedio"
        "advanced" -> "Avanzado"; else -> difficulty
    }
}

data class AppliedFilters(
    @SerializedName("language") val language: String,
    @SerializedName("pricing") val pricing: String,
    @SerializedName("types") val types: List<String>
)

data class ResponseMeta(
    @SerializedName("provider") val provider: String,
    @SerializedName("model") val model: String,
    @SerializedName("tier") val tier: String,
    @SerializedName("remainingQuota") val remainingQuota: Int?
)

// ═══════════════════════════════════════════════════════════
// PROVIDERS
// ═══════════════════════════════════════════════════════════

data class ProvidersResponse(
    @SerializedName("providers") val providers: List<ProviderInfo>,
    @SerializedName("freeTier") val freeTier: FreeTierInfo
)

data class ProviderInfo(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("requiresKey") val requiresKey: Boolean,
    @SerializedName("keyUrl") val keyUrl: String,
    @SerializedName("models") val models: List<String>,
    @SerializedName("tier") val tier: String
) {
    val icon get() = when (id) {
        "claude" -> "\uD83D\uDFE3"; "openai" -> "\uD83D\uDFE2"
        "gemini" -> "\uD83D\uDD35"; "groq" -> "\uD83D\uDFE0"; else -> "\u26AA"
    }
}

data class FreeTierInfo(
    @SerializedName("enabled") val enabled: Boolean,
    @SerializedName("dailyLimit") val dailyLimit: Int,
    @SerializedName("provider") val provider: String,
    @SerializedName("model") val model: String
)

// ═══════════════════════════════════════════════════════════
// VALIDATE KEY
// ═══════════════════════════════════════════════════════════

data class ValidateKeyRequest(
    @SerializedName("provider") val provider: String,
    @SerializedName("apiKey") val apiKey: String
)

data class ValidateKeyResponse(
    @SerializedName("valid") val valid: Boolean,
    @SerializedName("provider") val provider: String,
    @SerializedName("message") val message: String
)

// ═══════════════════════════════════════════════════════════
// QUOTA
// ═══════════════════════════════════════════════════════════

data class QuotaResponse(
    @SerializedName("remaining") val remaining: Int,
    @SerializedName("dailyLimit") val dailyLimit: Int
)
