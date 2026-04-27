package com.example.scrollslayer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.data.local.entity.ResourceEntity
import com.example.scrollslayer.ui.theme.*
import com.example.scrollslayer.viewmodel.ResourceViewModel

@Composable
fun MissionDetailScreen(
    mission: MissionEntity,
    onBack: () -> Unit,
    viewModel: ResourceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(mission.id) {
        viewModel.loadResources(mission.id)
    }

    Scaffold(containerColor = BgColor) { innerPadding ->
        MissionDetailContent(
            paddingValues = innerPadding,
            mission = mission,
            resources = uiState.resources,
            onAddResource = { title, url, type ->
                viewModel.addResource(missionId = mission.id, title = title, url = url, type = type)
            },
            onDeleteResource = { resourceId ->
                viewModel.deleteResource(missionId = mission.id, resourceId = resourceId)
            },
            onBack = onBack
        )
    }
}

@Composable
private fun MissionDetailContent(
    paddingValues: PaddingValues,
    mission: MissionEntity,
    resources: List<ResourceEntity>,
    onAddResource: (String, String, String) -> Unit,
    onDeleteResource: (Long) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(paddingValues)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Sticky-style mission header (inside scroll but at top)
        item { MissionDetailHeader(mission = mission, onBack = onBack) }

        // Resources section label
        item {
            Text(
                text = "RECURSOS DE APRENDIZAJE",
                color = TextSecondary.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp)
            )
        }

        if (resources.isEmpty()) {
            item { EmptyResourcesState() }
        } else {
            items(resources, key = { it.id }) { resource ->
                ResourceCard(
                    resource = resource,
                    onDelete = { onDeleteResource(resource.id) }
                )
            }
        }

        // Divider ornament
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = GoldSoft.copy(alpha = 0.25f)
                )
                Text(text = "✦", color = GoldSoft.copy(alpha = 0.5f), fontSize = 12.sp)
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = GoldSoft.copy(alpha = 0.25f)
                )
            }
        }

        // Add resource form
        item {
            AddResourceCard(
                title = title,
                url = url,
                type = type,
                onTitleChange = { title = it },
                onUrlChange = { url = it },
                onTypeChange = { type = it },
                onSaveClick = {
                    if (title.isNotBlank() && url.isNotBlank() && type.isNotBlank()) {
                        onAddResource(title.trim(), url.trim(), type.trim())
                        title = ""
                        url = ""
                        type = ""
                    }
                }
            )
        }
    }
}

// ─── Mission Detail Header ────────────────────────────────────────────────────

@Composable
private fun MissionDetailHeader(mission: MissionEntity, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgColor)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Back nav + badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("← Misiones", fontSize = 13.sp)
            }
            MissionStatusBadge(isActive = mission.isActive)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = mission.title,
            color = TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )

        Text(
            text = mission.description,
            color = TextSecondary,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            lineHeight = 19.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { /* TODO: Mark complete */ },
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BgColor
                )
            ) {
                Text("Marcar completada hoy", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Volver", fontSize = 13.sp)
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            color = GoldSoft.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun MissionStatusBadge(isActive: Boolean) {
    val bg = if (isActive) Gold.copy(alpha = 0.14f) else GoldSoft.copy(alpha = 0.08f)
    val border = if (isActive) Gold.copy(alpha = 0.35f) else GoldSoft.copy(alpha = 0.2f)
    val color = if (isActive) Gold else TextSecondary.copy(alpha = 0.6f)
    val label = if (isActive) "ACTIVA" else "INACTIVA"

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text = label, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
    }
}

// ─── Resource Card ────────────────────────────────────────────────────────────

@Composable
private fun ResourceCard(resource: ResourceEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = resource.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ResourceTypeBadge(type = resource.type)
            }

            Text(
                text = resource.url,
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { /* TODO: open URL */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Abrir recurso", fontSize = 12.sp)
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Danger),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("Eliminar", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ResourceTypeBadge(type: String) {
    val (bg, border, color) = when (type.lowercase()) {
        "podcast"              -> Triple(AccentBlue.copy(alpha = 0.18f), AccentBlue.copy(alpha = 0.35f), AccentBlue)
        "video"                -> Triple(Danger.copy(alpha = 0.18f),     Danger.copy(alpha = 0.35f),     Danger)
        "article", "artículo", "articulo" -> Triple(Success.copy(alpha = 0.18f), Success.copy(alpha = 0.35f), Success)
        else                   -> Triple(GoldSoft.copy(alpha = 0.18f),   GoldSoft.copy(alpha = 0.35f),   Gold)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(
            text = type.replaceFirstChar { it.uppercase() },
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

// ─── Empty Resources State ────────────────────────────────────────────────────

@Composable
private fun EmptyResourcesState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "📖", fontSize = 28.sp)
            Text(
                text = "Sin recursos todavía",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Agrega videos, podcasts, artículos o foros para avanzar en esta misión.",
                color = TextSecondary,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp
            )
        }
    }
}

// ─── Add Resource Card ────────────────────────────────────────────────────────

@Composable
private fun AddResourceCard(
    title: String,
    url: String,
    type: String,
    onTitleChange: (String) -> Unit,
    onUrlChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val resourceTypes = listOf("Video", "Podcast", "Article", "Forum", "Book")
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✦  Agregar recurso",
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold.copy(alpha = 0.6f),
                unfocusedBorderColor = GoldSoft.copy(alpha = 0.3f),
                focusedLabelColor = Gold,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = Gold
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título del recurso", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("URL", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = fieldColors
            )

            // Type dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = type,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de recurso", fontSize = 13.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(PanelSecondary)
                ) {
                    resourceTypes.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(option, color = TextPrimary, fontSize = 14.sp)
                            },
                            onClick = {
                                onTypeChange(option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold,
                        contentColor = BgColor
                    )
                ) {
                    Text("Guardar recurso", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = {
                        onTitleChange("")
                        onUrlChange("")
                        onTypeChange("")
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancelar", fontSize = 13.sp)
                }
            }
        }
    }
}
