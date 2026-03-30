package com.example.scrollslayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.ui.theme.BgColor
import com.example.scrollslayer.ui.theme.Gold
import com.example.scrollslayer.ui.theme.GoldSoft
import com.example.scrollslayer.ui.theme.PanelColor
import com.example.scrollslayer.ui.theme.TextPrimary
import com.example.scrollslayer.ui.theme.TextSecondary
import com.example.scrollslayer.viewmodel.MissionViewModel

@Composable
fun MissionsScreen(
    onBack: () -> Unit,
    onMissionSelected: (MissionEntity) -> Unit,
    viewModel: MissionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgColor
    ) { innerPadding ->
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(paddingValues)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("← Volver")
        }

        HeaderSection(activeMission = activeMission)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Tus misiones",
                    color = Gold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (missions.isEmpty()) {
                item {
                    EmptyMissionsState()
                }
            } else {
                items(missions, key = { it.id }) { mission ->
                    MissionCard(
                        mission = mission,
                        onSetActive = { onSetActiveMission(mission) },
                        onDelete = { onDeleteMission(mission.id) },
                        onClick = { onMissionSelected(mission) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

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

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection(
    activeMission: MissionEntity?
) {
    Surface(
        color = PanelColor,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldSoft, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tablón de misiones",
                color = Gold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Administra tus objetivos y elige cuál será tu misión activa.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (activeMission != null) {
                    "Misión activa: ${activeMission.title}"
                } else {
                    "Aún no tienes una misión activa"
                },
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

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
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nueva misión",
                color = Gold,
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = createAsActive,
                    onCheckedChange = onCreateAsActiveChange
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Marcar como misión activa",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BgColor
                )
            ) {
                Text("Guardar misión")
            }
        }
    }
}

@Composable
private fun MissionCard(
    mission: MissionEntity,
    onSetActive: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = PanelColor
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = mission.title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = mission.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (mission.isActive) "Activa" else "Inactiva",
                color = if (mission.isActive) Gold else TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!mission.isActive) {
                    Button(
                        onClick = onSetActive,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = BgColor
                        )
                    ) {
                        Text("Activar")
                    }
                }

                TextButton(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
private fun EmptyMissionsState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No hay misiones todavía",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Crea tu primera misión para empezar a redirigir tu tiempo.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}