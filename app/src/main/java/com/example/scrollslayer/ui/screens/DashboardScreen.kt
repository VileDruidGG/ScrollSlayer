package com.example.scrollslayer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scrollslayer.ui.theme.AccentBlue
import com.example.scrollslayer.ui.theme.BgColor
import com.example.scrollslayer.ui.theme.Danger
import com.example.scrollslayer.ui.theme.Gold
import com.example.scrollslayer.ui.theme.GoldSoft
import com.example.scrollslayer.ui.theme.PanelColor
import com.example.scrollslayer.ui.theme.PanelSecondary
import com.example.scrollslayer.ui.theme.Success
import com.example.scrollslayer.ui.theme.TextPrimary
import com.example.scrollslayer.ui.theme.TextSecondary
import com.example.scrollslayer.viewmodel.DashboardViewModel
import com.example.scrollslayer.data.model.SocialUsage
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.scrollslayer.utils.UsagePermissionChecker

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenMissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.loadDashboard()
    }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboard()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = BgColor
    ) { innerPadding ->
        DashboardContent(
            paddingValues = innerPadding,
            hasUsagePermission = uiState.hasUsagePermission,
            socialApps = uiState.socialApps,
            totalMinutes = uiState.totalMinutes,
            goalName = uiState.goalName,
            resourceTitle = "Podcast francés básico",
            savedResources = 3,
            onRefresh = viewModel::loadDashboard,
            onGrantPermission = { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))},
            onResumeMission = { /* TODO */ },
            onOpenMissions = onOpenMissions
        )
    }
}

@Composable
private fun DashboardContent(
    paddingValues: PaddingValues,
    hasUsagePermission: Boolean,
    socialApps: List<SocialUsage>,
    totalMinutes: Int,
    goalName: String,
    resourceTitle: String,
    savedResources: Int,
    onRefresh: () -> Unit,
    onGrantPermission: () -> Unit,
    onResumeMission: () -> Unit,
    onOpenMissions: () -> Unit
) {
    val maxDangerMinutes = 180f
    val progress = (totalMinutes / maxDangerMinutes).coerceIn(0f, 1f)
    val context = LocalContext.current
    val hasPermission = UsagePermissionChecker.hasUsageStatsPermission(context)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(paddingValues)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderSection()

        if (!hasUsagePermission) {
            PermissionCard(
                onGrantPermission = onGrantPermission
            )
        } else {
            EnemiesCard(
                socialApps = socialApps,
                totalMinutes = totalMinutes,
                dangerProgress = progress
            )
        }

        MissionActionCard(
            goalName = goalName,
            resourceTitle = resourceTitle,
            onResumeMission = onResumeMission,
            onOpenMissions = onOpenMissions
        )

        StatsSection(
            savedResources = savedResources,
            recoverableMinutes = totalMinutes,
            notificationsEnabled = true
        )

        Button(
            onClick = onRefresh,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = BgColor
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Actualizar tablero")
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun HeaderSection() {
    Surface(
        color = PanelColor,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = GoldSoft,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Gold.copy(alpha = 0.15f))
                        .border(1.dp, GoldSoft, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚔️",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "ScrollSlayer",
                        color = Gold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Recupera tu atención. Cumple tu misión.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Buen día, aventurero.",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Hoy tu enfoque se enfrenta a varios enemigos. Aún puedes recuperar el control.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun EnemiesCard(
    socialApps: List<SocialUsage>,
    totalMinutes: Int,
    dangerProgress: Float
) {
    FantasyCard(
        title = "Enemigos del día",
        titleColor = Danger
    ) {
        Text(
            text = "$totalMinutes min consumidos en total",
            color = TextPrimary,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "El doomscroll ha dispersado tu energía entre varios frentes.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        socialApps.forEachIndexed { index, app ->
            SocialUsageRow(app = app )

            if (index != socialApps.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Peligro total",
            color = TextSecondary,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { dangerProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = Danger,
            trackColor = PanelSecondary
        )
    }
}

@Composable
private fun PermissionCard(
    onGrantPermission: () -> Unit
) {
    FantasyCard(
        title = "Permiso requerido",
        titleColor = Danger
    ) {
        Text(
            text = "ScrollSlayer necesita acceso al uso de apps para detectar cuánto tiempo pasas en redes sociales.",
            color = TextPrimary,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Actívalo en la configuración del sistema para empezar a rastrear a los enemigos del día.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGrantPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = Danger,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Conceder acceso")
        }
    }
}
@Composable
private fun SocialUsageRow(
    app: SocialUsage
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PanelSecondary.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Danger.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = app.icon)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = app.appName,
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Tiempo consumido hoy",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = "${app.minutes} min",
            color = Gold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun MissionActionCard(
    goalName: String,
    resourceTitle: String,
    onResumeMission: () -> Unit,
    onOpenMissions: () -> Unit
) {
    FantasyCard(
        title = "Misión activa",
        titleColor = Gold
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Success.copy(alpha = 0.18f))
                    .border(
                        width = 1.dp,
                        color = Success.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📜")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = goalName,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Tu meta sigue en pie. Aún puedes retomar el camino.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(PanelSecondary.copy(alpha = 0.7f))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Siguiente acción",
                    color = AccentBlue,
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = resourceTitle,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Usa unos minutos recuperados para avanzar en tu misión.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = onResumeMission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Retomar misión")
            }
            Button(
                onClick = onOpenMissions,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)) {
                Text("Ver misiones")
            }
        }
    }
}

@Composable
private fun StatsSection(
    savedResources: Int,
    recoverableMinutes: Int,
    notificationsEnabled: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Estado del reino",
            color = Gold,
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = savedResources.toString(),
                label = "Recursos"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                value = "${recoverableMinutes}m",
                label = "Recuperables"
            )

            StatCard(
                modifier = Modifier.weight(1f),
                value = if (notificationsEnabled) "ON" else "OFF",
                label = "Alertas"
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.65f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun FantasyCard(
    title: String,
    titleColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = GoldSoft.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                color = titleColor,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}