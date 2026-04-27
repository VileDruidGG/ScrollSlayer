package com.example.scrollslayer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.scrollslayer.data.model.SocialUsage
import com.example.scrollslayer.ui.theme.*
import com.example.scrollslayer.viewmodel.DashboardViewModel
import android.content.Intent
import android.provider.Settings

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    contentPadding: PaddingValues = PaddingValues(),
    onOpenMissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.loadDashboard() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadDashboard()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(contentPadding)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DashboardHeader()

        if (!uiState.hasUsagePermission) {
            PermissionCard(
                onGrantPermission = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )
        } else {
            EnemiesCard(
                socialApps = uiState.socialApps,
                totalMinutes = uiState.totalMinutes
            )
        }

        MissionCard(
            goalName = uiState.goalName,
            onResumeMission = { /* TODO */ },
            onOpenMissions = onOpenMissions
        )

        SuggestedResourceCard(
            resourceTitle = "BBC French Listening",
            resourceType = "Podcast",
            resourceSource = "bbc.co.uk · 12 min"
        )

        CompanionCard()

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ─── Header ────────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "ScrollSlayer",
                color = Gold,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "\" Tu tiempo de hoy tiene consecuencias.\"",
                color = TextSecondary,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic
            )
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(50))
                .background(PanelColor)
                .border(1.dp, GoldSoft.copy(alpha = 0.5f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⚙", fontSize = 16.sp, color = Gold)
        }
    }
}

// ─── Enemies Card ───────────────────────────────────────────────────────────────────

@Composable
private fun EnemiesCard(
    socialApps: List<SocialUsage>,
    totalMinutes: Int
) {
    DashboardCard {
        Text(
            text = "⚔  Actividad enemiga hoy",
            color = Danger,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )
        Text(
            text = "Apps consumiendo tu atención ahora",
            color = TextSecondary,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        socialApps.forEachIndexed { index, app ->
            EnemyRow(app = app)
            if (index < socialApps.lastIndex) {
                HorizontalDivider(color = GoldSoft.copy(alpha = 0.12f), thickness = 1.dp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Danger.copy(alpha = 0.12f))
                .border(1.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exposición social total hoy",
                    color = Danger,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "$totalMinutes min",
                    color = Danger,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Ver uso detallado", fontSize = 13.sp)
            }
            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Danger.copy(alpha = 0.35f),
                    contentColor = Danger
                )
            ) {
                Text("Restringir ahora", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun EnemyRow(app: SocialUsage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Danger.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = app.icon, fontSize = 18.sp)
        }

        Text(
            text = app.appName,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${app.minutes} min",
            color = Danger,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(RoundedCornerShape(50))
                .background(Danger)
        )
    }
}

// ─── Mission Card ────────────────────────────────────────────────────────────────────

@Composable
private fun MissionCard(
    goalName: String,
    onResumeMission: () -> Unit,
    onOpenMissions: () -> Unit
) {
    DashboardCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "⚑  Misión actual",
                color = Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Gold.copy(alpha = 0.14f))
                    .border(1.dp, Gold.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "ACTIVA",
                    color = Gold,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = goalName,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )

        Text(
            text = "Completa tu práctica diaria para avanzar en tu misión.",
            color = TextSecondary,
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(top = 3.dp, bottom = 14.dp)
        )

        LinearProgressIndicator(
            progress = { 0.62f },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Gold,
            trackColor = PanelSecondary
        )
        Text(
            text = "62% completado hoy",
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            textAlign = TextAlign.End
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onResumeMission,
                modifier = Modifier.weight(2f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = BgColor
                )
            ) {
                Text("Continuar misión", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onOpenMissions,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Cambiar", fontSize = 13.sp)
            }
        }
    }
}

// ─── Suggested Resource Card ──────────────────────────────────────────────────────────────────

@Composable
private fun SuggestedResourceCard(
    resourceTitle: String,
    resourceType: String,
    resourceSource: String
) {
    DashboardCard {
        Text(
            text = "✦  Acción sugerida",
            color = AccentBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = resourceTitle,
            color = TextPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        ) {
            ResourceTypeBadge(type = resourceType)
            Text(text = resourceSource, color = TextSecondary, fontSize = 11.sp)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue.copy(alpha = 0.3f),
                    contentColor = AccentBlue
                )
            ) {
                Text("Abrir recurso", fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Saltar", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ResourceTypeBadge(type: String) {
    val (bg, border, text) = when (type.lowercase()) {
        "podcast"               -> Triple(AccentBlue.copy(alpha = 0.18f), AccentBlue.copy(alpha = 0.35f), AccentBlue)
        "video"                 -> Triple(Danger.copy(alpha = 0.18f),     Danger.copy(alpha = 0.35f),     Danger)
        "artículo", "articulo"  -> Triple(Success.copy(alpha = 0.18f),    Success.copy(alpha = 0.35f),    Success)
        else                    -> Triple(GoldSoft.copy(alpha = 0.18f),   GoldSoft.copy(alpha = 0.35f),   Gold)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp)
    ) {
        Text(text = type, color = text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Companion Card ─────────────────────────────────────────────────────────────────────────

@Composable
private fun CompanionCard() {
    DashboardCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PanelSecondary)
                    .border(1.dp, GoldSoft.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧙", fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "El Archivista Mago",
                    color = Gold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "\"Incluso diez minutos enfocados reconfiguran el campo de batalla.\"",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 18.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { },
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Ignorar", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedButton(
                onClick = { },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("Cambiar compañero", fontSize = 12.sp)
            }
        }
    }
}

// ─── Permission Card ─────────────────────────────────────────────────────────────────────

@Composable
private fun PermissionCard(onGrantPermission: () -> Unit) {
    DashboardCard {
        Text(
            text = "⚠  Permiso requerido",
            color = Danger,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "ScrollSlayer necesita acceso al uso de apps para detectar a los enemigos del día.",
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Actívalo en la configuración del sistema para empezar.",
            color = TextSecondary,
            fontSize = 13.sp,
            fontStyle = FontStyle.Italic
        )
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onGrantPermission,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Danger,
                contentColor = Color.White
            )
        ) {
            Text("Conceder acceso")
        }
    }
}

// ─── Base Card ────────────────────────────────────────────────────────────────────────────────

@Composable
private fun DashboardCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, GoldSoft.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            content = content
        )
    }
}
