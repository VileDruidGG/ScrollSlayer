package com.example.scrollslayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scrollslayer.data.local.ApiKeyStore
import com.example.scrollslayer.data.remote.*
import com.example.scrollslayer.data.repository.LearnPathRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════

data class LearnPathUiState(
    // Search
    val goal: String = "",
    val selectedLanguage: String = "es",
    val selectedPricing: String = "all",
    val selectedTypes: Set<String> = setOf(
        "video", "podcast", "article", "documentation", "course"
    ),
    val showFilters: Boolean = false,

    // Loading & results
    val isLoading: Boolean = false,
    val response: GoalResponse? = null,
    val error: String? = null,

    // Post-search local filters
    val activeTypeFilter: String? = null,
    val activePricingFilter: String? = null,

    // Multi-provider
    val providers: List<ProviderInfo> = emptyList(),
    val freeTier: FreeTierInfo? = null,
    val activeProvider: String? = null,
    val configuredKeys: Set<String> = emptySet(),
    val remainingQuota: Int? = null,

    // Key config dialog
    val showKeyConfig: Boolean = false,
    val keyConfigProvider: String? = null,
    val keyInput: String = "",
    val isValidatingKey: Boolean = false,
    val keyValidationResult: String? = null,

    // Last result meta
    val lastUsedProvider: String? = null,
    val lastUsedModel: String? = null,
    val lastUsedTier: String? = null
) {
    val languages: List<Pair<String, String>> get() = listOf(
        "es" to "Español", "en" to "English", "fr" to "Français",
        "pt" to "Português", "de" to "Deutsch"
    )

    val resourceTypes: List<Triple<String, String, String>> get() = listOf(
        Triple("video", "Videos", "\uD83C\uDFAC"),
        Triple("podcast", "Podcasts", "\uD83C\uDF99\uFE0F"),
        Triple("article", "Artículos", "\uD83D\uDCDD"),
        Triple("documentation", "Docs", "\uD83D\uDCDA"),
        Triple("course", "Cursos", "\uD83C\uDF93")
    )
}

// ═══════════════════════════════════════════════════════════
// VIEW MODEL
// ═══════════════════════════════════════════════════════════

class LearnPathViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val keyStore = ApiKeyStore(application)
    private val repository = LearnPathRepository(
        api = RetrofitClient.api,
        keyStore = keyStore
    )

    private val _uiState = MutableStateFlow(LearnPathUiState())
    val uiState: StateFlow<LearnPathUiState> = _uiState.asStateFlow()

    val filteredResources: List<LearningResource>
        get() {
            val all = _uiState.value.response?.resources ?: return emptyList()
            return all.filter { r ->
                val tm = _uiState.value.activeTypeFilter?.let { r.type == it } ?: true
                val pm = _uiState.value.activePricingFilter?.let { r.pricing == it } ?: true
                tm && pm
            }
        }

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update {
            it.copy(
                configuredKeys = keyStore.getConfiguredProviders(),
                activeProvider = keyStore.getActiveProvider()
            )
        }

        viewModelScope.launch {
            when (val result = repository.getProviders()) {
                is LearnPathRepository.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            providers = result.data.providers,
                            freeTier = result.data.freeTier
                        )
                    }
                }
                is LearnPathRepository.Result.Error -> {}
            }
        }

        viewModelScope.launch {
            when (val result = repository.getQuota()) {
                is LearnPathRepository.Result.Success -> {
                    _uiState.update { it.copy(remainingQuota = result.data.remaining) }
                }
                is LearnPathRepository.Result.Error -> {}
            }
        }
    }

    // ─── Search actions ─────────────────────────────────────

    fun updateGoal(goal: String) {
        _uiState.update { it.copy(goal = goal, error = null) }
    }

    fun updateLanguage(code: String) {
        _uiState.update { it.copy(selectedLanguage = code) }
    }

    fun updatePricing(pricing: String) {
        _uiState.update { it.copy(selectedPricing = pricing) }
    }

    fun toggleResourceType(typeId: String) {
        _uiState.update { state ->
            val current = state.selectedTypes.toMutableSet()
            if (current.contains(typeId)) {
                if (current.size > 1) current.remove(typeId)
            } else {
                current.add(typeId)
            }
            state.copy(selectedTypes = current)
        }
    }

    fun toggleFilters() {
        _uiState.update { it.copy(showFilters = !it.showFilters) }
    }

    fun setLocalTypeFilter(type: String?) {
        _uiState.update { it.copy(activeTypeFilter = type) }
    }

    fun setLocalPricingFilter(pricing: String?) {
        _uiState.update { it.copy(activePricingFilter = pricing) }
    }

    fun clearResults() {
        _uiState.update {
            it.copy(
                response = null, error = null,
                activeTypeFilter = null, activePricingFilter = null
            )
        }
    }

    fun searchResources() {
        val state = _uiState.value

        if (state.goal.isBlank()) {
            _uiState.update { it.copy(error = "Escribe una meta para buscar recursos") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, error = null, response = null,
                    activeTypeFilter = null, activePricingFilter = null
                )
            }

            when (val result = repository.findResources(
                goal = state.goal,
                language = state.selectedLanguage,
                pricing = state.selectedPricing,
                types = state.selectedTypes.toList()
            )) {
                is LearnPathRepository.Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            response = result.data,
                            lastUsedProvider = result.data.meta.provider,
                            lastUsedModel = result.data.meta.model,
                            lastUsedTier = result.data.meta.tier,
                            remainingQuota = result.data.meta.remainingQuota
                        )
                    }
                }
                is LearnPathRepository.Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    // ─── Provider & key management ──────────────────────────

    fun selectProvider(providerId: String?) {
        _uiState.update { it.copy(activeProvider = providerId) }
        keyStore.setActiveProvider(providerId)
    }

    fun openKeyConfig(providerId: String) {
        _uiState.update {
            it.copy(
                showKeyConfig = true,
                keyConfigProvider = providerId,
                keyInput = "",
                keyValidationResult = null
            )
        }
    }

    fun closeKeyConfig() {
        _uiState.update {
            it.copy(
                showKeyConfig = false,
                keyConfigProvider = null,
                keyInput = "",
                keyValidationResult = null
            )
        }
    }

    fun updateKeyInput(key: String) {
        _uiState.update { it.copy(keyInput = key, keyValidationResult = null) }
    }

    fun validateAndSaveKey() {
        val state = _uiState.value
        val provider = state.keyConfigProvider ?: return
        val key = state.keyInput.trim()

        if (key.isBlank()) {
            _uiState.update { it.copy(keyValidationResult = "Ingresa tu API key") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isValidatingKey = true) }

            when (val result = repository.validateKey(provider, key)) {
                is LearnPathRepository.Result.Success -> {
                    if (result.data.valid) {
                        keyStore.saveKey(provider, key)
                        keyStore.setActiveProvider(provider)
                        _uiState.update {
                            it.copy(
                                isValidatingKey = false,
                                keyValidationResult = "Key válida y guardada",
                                configuredKeys = it.configuredKeys + provider,
                                activeProvider = provider,
                                showKeyConfig = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isValidatingKey = false,
                                keyValidationResult = "Key inválida. Verifica e intenta de nuevo."
                            )
                        }
                    }
                }
                is LearnPathRepository.Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isValidatingKey = false,
                            keyValidationResult = "Error: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    fun removeKey(providerId: String) {
        keyStore.removeKey(providerId)
        val newActive = if (_uiState.value.activeProvider == providerId) null
                        else _uiState.value.activeProvider
        _uiState.update {
            it.copy(
                configuredKeys = it.configuredKeys - providerId,
                activeProvider = newActive
            )
        }
        keyStore.setActiveProvider(newActive)
    }
}
