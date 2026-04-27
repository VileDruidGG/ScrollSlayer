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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.ui.theme.*
import com.example.scrollslayer.viewmodel.MissionViewModel

@Composable
fun MissionsScreen(
    onBack: () -> Unit,
    onMissionSelected: (MissionEntity) -> Unit,
    viewModel: MissionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(containerColor = BgColor) { innerPadding ->
        MissionsContent(
            paddingValues = innerPadding,
            missions = uiState.missions,
            activeMission = uiState.activeMission,
            onCreateMission = viewModel::createMission,
            onSetActiveMission = viewModel::setActiveMission,
            onDeleteMission = viewModel::deleteMission,
            onMissionSelected = onMissionSelected,
            onBack = onBack
        )
    }
}

@Composable
private fun MissionsContent(
    paddingValues: PaddingValues,
    missions: List<MissionEntity>,
    activeMission: MissionEntity?,
    onCreateMission: (String, String, Boolean) -> Unit,
    onSetActiveMission: (MissionEntity) -> Unit,
    onDeleteMission: (Long) -> Unit,
    onMissionSelected: (MissionEntity) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var createAsActive by remember { mutableStateOf(false) }
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
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
    ) {
        // Back nav
        item {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("← Misiones", fontSize = 13.sp)
            }
        }

        // Board header
        item { BoardHeader(activeMission = activeMission) }

        // Section label
        item {
            Text(
                text = "CONTRATOS ACTIVOS",
                color = TextSecondary.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.8.sp,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
            )
        }

        if (missions.isEmpty()) {
            item { EmptyMissionsState() }
        } else {
            items(missions, key = { it.id }) { mission ->
                MissionBoardCard(
                    mission = mission,
                    onSetActive = { onSetActiveMission(mission) },
                    onDelete = { onDeleteMission(mission.id) },
                    onClick = { onMissionSelected(mission) }
                )
            }
        }

        // Divider ornament
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

        // Create mission form
        item {
            CreateMissionCard(
                title = title,
                description = description,
                createAsActive = createAsActive,
                onTitleChange = { title = it },
                onDescriptionChange = { description = it },
                onCreateAsActiveChange = { createAsActive = it },
                onSaveClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onCreateMission(title.trim(), description.trim(), createAsActive)
                        title = ""
                        description = ""
                        createAsActive = false
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

// ─── Board Header ─────────────────────────────────────────────────────────────

@Composable
private fun BoardHeader(activeMission: MissionEntity?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tablón de Misiones",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        Text(
            text = "Elige qué objetivo merece tu tiempo.",
            color = TextSecondary,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Active mission banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Gold.copy(alpha = 0.10f))
                .border(1.dp, Gold.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⚑", fontSize = 14.sp, color = Gold)
                Text(
                    text = "Misión activa: ",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = activeMission?.title ?: "Ninguna",
                    color = Gold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ─── Mission Board Card ───────────────────────────────────────────────────────

@Composable
private fun MissionBoardCard(
    mission: MissionEntity,
    onSetActive: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Title + badge row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = mission.title,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MissionStatusBadge(isActive = mission.isActive)
            }

            Text(
                text = mission.description,
                color = TextSecondary,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!mission.isActive) {
                    Button(
                        onClick = onSetActive,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = BgColor
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Activar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Abrir", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = Danger),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Text("Eliminar", fontSize = 12.sp)
                }
            }
        }
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
        Text(
            text = label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyMissionsState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "⚑", fontSize = 32.sp)
            Text(
                text = "Sin misiones todavía",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Crea tu primera misión para empezar a redirigir tu tiempo hacia algo que importe.",
                color = TextSecondary,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 18.sp
            )
        }
    }
}

// ─── Create Mission Card ──────────────────────────────────────────────────────

@Composable
private fun CreateMissionCard(
    title: String,
    description: String,
    createAsActive: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onCreateAsActiveChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✦  Crear nueva misión",
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título de la misión", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold.copy(alpha = 0.6f),
                    unfocusedBorderColor = GoldSoft.copy(alpha = 0.3f),
                    focusedLabelColor = Gold,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Gold
                )
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold.copy(alpha = 0.6f),
                    unfocusedBorderColor = GoldSoft.copy(alpha = 0.3f),
                    focusedLabelColor = Gold,
                    unfocusedLabelColor = TextSecondary,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Gold
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = createAsActive,
                    onCheckedChange = onCreateAsActiveChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Gold,
                        uncheckedColor = GoldSoft.copy(alpha = 0.5f),
                        checkmarkColor = BgColor
                    )
                )
                Text(
                    text = "Marcar como misión activa",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
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
                    Text("Guardar misión", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                OutlinedButton(
                    onClick = {
                        onTitleChange("")
                        onDescriptionChange("")
                        onCreateAsActiveChange(false)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.4f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Limpiar", fontSize = 13.sp)
                }
            }
        }
    }
}
