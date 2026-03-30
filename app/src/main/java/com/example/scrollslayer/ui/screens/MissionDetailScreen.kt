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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scrollslayer.data.local.entity.MissionEntity
import com.example.scrollslayer.data.local.entity.ResourceEntity
import com.example.scrollslayer.ui.theme.BgColor
import com.example.scrollslayer.ui.theme.Gold
import com.example.scrollslayer.ui.theme.GoldSoft
import com.example.scrollslayer.ui.theme.PanelColor
import com.example.scrollslayer.ui.theme.TextPrimary
import com.example.scrollslayer.ui.theme.TextSecondary
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

    Scaffold(
        containerColor = BgColor
    ) { innerPadding ->
        MissionDetailContent(
            paddingValues = innerPadding,
            mission = mission,
            resources = uiState.resources,
            onAddResource = { title, url, type ->
                viewModel.addResource(
                    missionId = mission.id,
                    title = title,
                    url = url,
                    type = type
                )
            },
            onDeleteResource = { resourceId ->
                viewModel.deleteResource(
                    missionId = mission.id,
                    resourceId = resourceId
                )
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

        MissionHeader(mission = mission)

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Recursos de la misión",
                    color = Gold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (resources.isEmpty()) {
                item {
                    EmptyResourcesState()
                }
            } else {
                items(resources, key = { it.id }) { resource ->
                    ResourceCard(
                        resource = resource,
                        onDelete = { onDeleteResource(resource.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

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
                            onAddResource(
                                title.trim(),
                                url.trim(),
                                type.trim()
                            )
                            title = ""
                            url = ""
                            type = ""
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
private fun MissionHeader(
    mission: MissionEntity
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
                text = mission.title,
                color = Gold,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = mission.description,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = if (mission.isActive) "Misión activa" else "Misión inactiva",
                color = if (mission.isActive) Gold else TextSecondary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

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
                text = "Agregar recurso",
                color = Gold,
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título del recurso") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = url,
                onValueChange = onUrlChange,
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = type,
                onValueChange = onTypeChange,
                label = { Text("Tipo (video, podcast, article, forum)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BgColor
                )
            ) {
                Text("Guardar recurso")
            }
        }
    }
}

@Composable
private fun ResourceCard(
    resource: ResourceEntity,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
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
                text = resource.title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = resource.type,
                color = Gold,
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = resource.url,
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                TextButton(onClick = onDelete) {
                    Text("Eliminar")
                }
            }
        }
    }
}

@Composable
private fun EmptyResourcesState() {
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
                text = "No hay recursos todavía",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Agrega videos, artículos, podcasts o foros para avanzar en esta misión.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}