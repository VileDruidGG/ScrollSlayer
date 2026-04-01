package com.example.scrollslayer.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.remote.LearningResource
import com.example.scrollslayer.viewmodel.LearnPathUiState
import com.example.scrollslayer.viewmodel.LearnPathViewModel

// ═══════════════════════════════════════════════════════════
// MAIN SCREEN
// ═══════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnPathScreen(
    onBack: () -> Unit,
    viewModel: LearnPathViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showKeyConfig) {
        KeyConfigDialog(
            providerName = uiState.providers
                .find { it.id == uiState.keyConfigProvider }?.name ?: "",
            keyUrl = uiState.providers
                .find { it.id == uiState.keyConfigProvider }?.keyUrl ?: "",
            keyInput = uiState.keyInput,
            isValidating = uiState.isValidatingKey,
            validationResult = uiState.keyValidationResult,
            onKeyChange = viewModel::updateKeyInput,
            onValidate = viewModel::validateAndSaveKey,
            onDismiss = viewModel::closeKeyConfig
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LearnPath", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    uiState.lastUsedTier?.let { tier ->
                        Surface(
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = when (tier) {
                                    "free" -> "Free (${uiState.remainingQuota ?: "?"})"
                                    "byok" -> "Tu key"
                                    "server" -> "Pro"
                                    else -> tier
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    if (uiState.response != null) {
                        IconButton(onClick = { viewModel.clearResults() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Nueva búsqueda",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.response == null) {
                SearchSection(
                    uiState = uiState,
                    onGoalChange = viewModel::updateGoal,
                    onLanguageChange = viewModel::updateLanguage,
                    onPricingChange = viewModel::updatePricing,
                    onToggleType = viewModel::toggleResourceType,
                    onToggleFilters = viewModel::toggleFilters,
                    onSearch = viewModel::searchResources,
                    onOpenKeyConfig = viewModel::openKeyConfig,
                    onSelectProvider = viewModel::selectProvider,
                    onRemoveKey = viewModel::removeKey
                )
            } else {
                ResultsSection(
                    uiState = uiState,
                    filteredResources = viewModel.filteredResources,
                    onTypeFilter = viewModel::setLocalTypeFilter,
                    onPricingFilter = viewModel::setLocalPricingFilter
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// SEARCH
// ═══════════════════════════════════════════════════════════

@Composable
private fun SearchSection(
    uiState: LearnPathUiState,
    onGoalChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onPricingChange: (String) -> Unit,
    onToggleType: (String) -> Unit,
    onToggleFilters: () -> Unit,
    onSearch: () -> Unit,
    onOpenKeyConfig: (String) -> Unit,
    onSelectProvider: (String?) -> Unit,
    onRemoveKey: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¿Qué quieres aprender?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Escribe tu meta y la IA encontrará los mejores recursos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = uiState.goal,
            onValueChange = onGoalChange,
            label = { Text("Tu meta de aprendizaje") },
            placeholder = { Text("Ej: Aprender a cocinar pasta italiana") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = false,
            maxLines = 3
        )

        OutlinedButton(
            onClick = onToggleFilters,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                if (uiState.showFilters) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (uiState.showFilters) "Ocultar filtros" else "Filtros y proveedor IA")
        }

        AnimatedVisibility(visible = uiState.showFilters) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Language
                    Text("Idioma", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        uiState.languages.forEach { (code, name) ->
                            FilterChip(
                                selected = uiState.selectedLanguage == code,
                                onClick = { onLanguageChange(code) },
                                label = { Text(name, fontSize = 12.sp) }
                            )
                        }
                    }

                    HorizontalDivider()

                    // Pricing
                    Text("Precio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("all" to "Todos", "free" to "Gratis", "paid" to "De pago")
                            .forEach { (value, label) ->
                                FilterChip(
                                    selected = uiState.selectedPricing == value,
                                    onClick = { onPricingChange(value) },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                    }

                    HorizontalDivider()

                    // Resource types
                    Text("Tipos de recurso", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        uiState.resourceTypes.forEach { (id, label, icon) ->
                            FilterChip(
                                selected = id in uiState.selectedTypes,
                                onClick = { onToggleType(id) },
                                label = { Text("$icon $label", fontSize = 12.sp) }
                            )
                        }
                    }

                    HorizontalDivider()

                    // AI Provider
                    Text("Proveedor de IA", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    // Free tier option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (uiState.activeProvider == null)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface
                            )
                            .clickable { onSelectProvider(null) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.activeProvider == null,
                            onClick = { onSelectProvider(null) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automático (gratis)", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(
                                "${uiState.remainingQuota ?: "?"} búsquedas restantes hoy",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // BYOK providers
                    uiState.providers.forEach { provider ->
                        val hasKey = provider.id in uiState.configuredKeys
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (uiState.activeProvider == provider.id)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable {
                                    if (hasKey) onSelectProvider(provider.id)
                                    else onOpenKeyConfig(provider.id)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.activeProvider == provider.id,
                                onClick = {
                                    if (hasKey) onSelectProvider(provider.id)
                                    else onOpenKeyConfig(provider.id)
                                },
                                enabled = hasKey
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${provider.icon} ${provider.name}",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    if (hasKey) "Key configurada" else provider.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (hasKey) {
                                IconButton(
                                    onClick = { onRemoveKey(provider.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar key",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                TextButton(onClick = { onOpenKeyConfig(provider.id) }) {
                                    Text("Agregar", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Error
        uiState.error?.let { error ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(error, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Search button
        Button(
            onClick = onSearch,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && uiState.goal.isNotBlank(),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Buscando con IA...")
            } else {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Buscar recursos", fontSize = 16.sp)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// RESULTS
// ═══════════════════════════════════════════════════════════

@Composable
private fun ResultsSection(
    uiState: LearnPathUiState,
    filteredResources: List<LearningResource>,
    onTypeFilter: (String?) -> Unit,
    onPricingFilter: (String?) -> Unit
) {
    val response = uiState.response ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = response.goal,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = response.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    uiState.lastUsedProvider?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Generado con ${uiState.lastUsedModel ?: it}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Roadmap
        item {
            Text("Ruta de aprendizaje",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }
        itemsIndexed(response.roadmap) { index, step ->
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Text(step, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Result filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Recursos (${filteredResources.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    FilterChip(
                        selected = uiState.activeTypeFilter == null,
                        onClick = { onTypeFilter(null) },
                        label = { Text("Todos", fontSize = 12.sp) }
                    )
                    uiState.resourceTypes.forEach { (id, _, icon) ->
                        val count = response.resources.count { it.type == id }
                        if (count > 0) {
                            FilterChip(
                                selected = uiState.activeTypeFilter == id,
                                onClick = { onTypeFilter(if (uiState.activeTypeFilter == id) null else id) },
                                label = { Text("$icon $count", fontSize = 12.sp) }
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = uiState.activePricingFilter == null,
                        onClick = { onPricingFilter(null) },
                        label = { Text("Todo", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = uiState.activePricingFilter == "free",
                        onClick = { onPricingFilter(if (uiState.activePricingFilter == "free") null else "free") },
                        label = { Text("Gratis", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = uiState.activePricingFilter == "paid",
                        onClick = { onPricingFilter(if (uiState.activePricingFilter == "paid") null else "paid") },
                        label = { Text("De pago", fontSize = 12.sp) }
                    )
                }
            }
        }

        // Resource cards
        items(filteredResources) { resource ->
            ResourceCard(resource = resource)
        }

        if (filteredResources.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay recursos con estos filtros",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// RESOURCE CARD
// ═══════════════════════════════════════════════════════════

@Composable
private fun ResourceCard(resource: LearningResource) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = { uriHandler.openUri(resource.url) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${resource.icon} ${resource.type}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    color = if (resource.pricing == "free")
                        Color(0xFF4CAF50).copy(alpha = 0.15f)
                    else Color(0xFFFF9800).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        resource.pricingLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Medium,
                        color = if (resource.pricing == "free") Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(resource.title, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(resource.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("\u23F1 ${resource.estimatedTime}", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("\uD83D\uDCCA ${resource.difficultyLabel}", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("\uD83D\uDCFA ${resource.platform}", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (resource.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    resource.tags.forEach { tag ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("#$tag",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// KEY CONFIG DIALOG
// ═══════════════════════════════════════════════════════════

@Composable
private fun KeyConfigDialog(
    providerName: String,
    keyUrl: String,
    keyInput: String,
    isValidating: Boolean,
    validationResult: String?,
    onKeyChange: (String) -> Unit,
    onValidate: () -> Unit,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    var showKey by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar $providerName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Ingresa tu API key para usar $providerName sin límites.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(onClick = { uriHandler.openUri(keyUrl) }) {
                    Text("Obtener key gratis \u2192", fontSize = 13.sp)
                }

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = onKeyChange,
                    label = { Text("API Key") },
                    placeholder = { Text("sk-... o AIza...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showKey)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Default.Lock else Icons.Default.Lock,
                                contentDescription = "Mostrar/ocultar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(10.dp)
                )

                validationResult?.let {
                    Text(
                        it,
                        fontSize = 13.sp,
                        color = if (it.contains("válida", ignoreCase = true))
                            Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    "Tu key se almacena cifrada solo en tu dispositivo.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onValidate,
                enabled = !isValidating && keyInput.isNotBlank()
            ) {
                if (isValidating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isValidating) "Validando..." else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
