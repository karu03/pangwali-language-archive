package org.pangwali.preservation.ui.screens.recordings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecordingsViewModel
) {
    val recordings by viewModel.recordings.collectAsState()
    val currentlyPlayingPath by viewModel.currentlyPlayingPath.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recordings Library") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (recordings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No recordings yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Start a field session from the home screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recordings) { rec ->
                    val isPlaying = currentlyPlayingPath == rec.audioPath
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rec.title ?: "Untitled Take", style = MaterialTheme.typography.titleMedium)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Speaker: ${rec.speakerId}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        text = rec.category.name.replace("_", " "),
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                                
                                val durationSecs = rec.durationMs / 1000.0
                                Text("Duration: ${String.format(Locale.getDefault(), "%.1fs", durationSecs)}", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            IconButton(onClick = { viewModel.playRecording(rec.audioPath) }) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Stop" else "Play"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
