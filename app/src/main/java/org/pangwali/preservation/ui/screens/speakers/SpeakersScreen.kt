package org.pangwali.preservation.ui.screens.speakers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.pangwali.preservation.data.db.AgeGroup
import org.pangwali.preservation.data.db.PangwaliVariant
import org.pangwali.preservation.data.db.RecordingEntity
import org.pangwali.preservation.data.db.SpeakerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakersScreen(
    onNavigateBack: () -> Unit,
    viewModel: SpeakersViewModel
) {
    val speakers by viewModel.speakers.collectAsState()
    val recordings by viewModel.recordings.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Speakers", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Speaker")
            }
        }
    ) { paddingValues ->
        if (speakers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No speakers found", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tap the + button to add a field speaker.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(12.dp)) }
                
                items(speakers) { speaker ->
                    val speakerRecordings = recordings.filter { it.speakerId == speaker.id }
                    SpeakerCard(speaker, speakerRecordings)
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        if (showAddDialog) {
            AddSpeakerDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, village, ageGroup, variant ->
                    viewModel.addSpeaker(name, village, ageGroup, variant, null, true)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun SpeakerCard(speaker: SpeakerEntity, recordings: List<RecordingEntity>) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = speaker.id.takeLast(3),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = speaker.id,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${speaker.ageGroup.name.replace("_", " ")} • ${speaker.village}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    val totalMins = recordings.sumOf { it.durationMs } / (1000 * 60)
                    Text(
                        text = "${totalMins}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${recordings.size} takes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (expanded && recordings.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recordings", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    recordings.take(5).forEach { rec ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rec.title ?: "Untitled",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = rec.category.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (recordings.size > 5) {
                        Text(
                            text = "+ ${recordings.size - 5} more",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddSpeakerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String?, String, AgeGroup, PangwaliVariant) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf(AgeGroup.AGE_30_50) }
    var variant by remember { mutableStateOf(PangwaliVariant.P_SACH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Speaker") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Private Name (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text("Village") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Column {
                    Text("Age Group", style = MaterialTheme.typography.labelSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AgeGroupChip(AgeGroup.UNDER_30, ageGroup == AgeGroup.UNDER_30) { ageGroup = AgeGroup.UNDER_30 }
                        AgeGroupChip(AgeGroup.AGE_30_50, ageGroup == AgeGroup.AGE_30_50) { ageGroup = AgeGroup.AGE_30_50 }
                        AgeGroupChip(AgeGroup.OVER_50, ageGroup == AgeGroup.OVER_50) { ageGroup = AgeGroup.OVER_50 }
                    }
                }

                Column {
                    Text("Pangwali Variant", style = MaterialTheme.typography.labelSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PangwaliVariant.entries.forEach { v ->
                            FilterChip(
                                selected = variant == v,
                                onClick = { variant = v },
                                label = { Text(v.name.replace("_", " ")) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(if (name.isBlank()) null else name, village, ageGroup, variant) },
                enabled = village.isNotBlank()
            ) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeGroupChip(group: AgeGroup, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { 
            Text(
                when(group) {
                    AgeGroup.UNDER_30 -> "<30"
                    AgeGroup.AGE_30_50 -> "30-50"
                    AgeGroup.OVER_50 -> "50+"
                    else -> "?"
                }
            )
        }
    )
}
