package org.pangwali.preservation.ui.screens.recording

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import org.pangwali.preservation.data.db.DatasetCategory
import org.pangwali.preservation.ui.components.WaveformVisualizer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawRecordingScreen(
    onNavigateBack: () -> Unit,
    viewModel: RawRecordingViewModel,
    contextViewModel: RecordingContextViewModel
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val timerMs by viewModel.timerMs.collectAsState()
    val amplitude by viewModel.amplitude.collectAsState()
    val reviewRecording by viewModel.reviewRecording.collectAsState()
    
    val activePromptTitle by contextViewModel.activePromptTitle.collectAsState()
    val activeCategory by contextViewModel.activeCategory.collectAsState()
    
    val speakers by viewModel.speakers.collectAsState()
    val selectedSpeakerId by viewModel.selectedSpeakerId.collectAsState()

    var selectedCategory by remember { mutableStateOf(activeCategory) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showSpeakerMenu by remember { mutableStateOf(false) }
    
    LaunchedEffect(activeCategory) {
        selectedCategory = activeCategory
    }
    
    // Auto-select first speaker if none selected
    LaunchedEffect(speakers) {
        if (selectedSpeakerId == null && speakers.isNotEmpty()) {
            viewModel.selectSpeaker(speakers.first().id)
        }
    }

    DisposableEffect(Unit) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            selectedSpeakerId?.let { viewModel.startRecording(it, selectedCategory, activePromptTitle) }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Field Recorder", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SPEAKER & CATEGORY INFO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // SPEAKER PICKER
                Surface(
                    onClick = { if (!isRecording) showSpeakerMenu = true },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedSpeakerId ?: "Select Speaker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (!isRecording) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }
                
                DropdownMenu(expanded = showSpeakerMenu, onDismissRequest = { showSpeakerMenu = false }) {
                    if (speakers.isEmpty()) {
                        DropdownMenuItem(text = { Text("No speakers found") }, onClick = { showSpeakerMenu = false })
                    }
                    speakers.forEach { speaker ->
                        DropdownMenuItem(
                            text = { Text("${speaker.id} (${speaker.village})") },
                            onClick = {
                                viewModel.selectSpeaker(speaker.id)
                                showSpeakerMenu = false
                            }
                        )
                    }
                }

                if (activePromptTitle != null) {
                    Text(
                        text = "PROMPT: $activePromptTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    onClick = { if (!isRecording) showCategoryMenu = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory.name.replace("_", " "),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!isRecording) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                
                DropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DatasetCategory.entries.filter { 
                        it == DatasetCategory.RAW_CONVERSATION || 
                        it == DatasetCategory.HINDI_PROMPT_RESPONSE || 
                        it == DatasetCategory.FULL_ELICITATION ||
                        it == DatasetCategory.STORYTELLING ||
                        it == DatasetCategory.WORDLIST
                    }.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name.replace("_", " ")) },
                            onClick = {
                                selectedCategory = category
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // TECHNICAL INFO
            Text(
                text = "WAV / 48 kHz / Mono",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            val minutes = (timerMs / 1000) / 60
            val seconds = (timerMs / 1000) % 60
            val millis = (timerMs % 1000) / 10
            
            Text(
                text = String.format("%02d:%02d.%02d", minutes, seconds, millis),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Light
                ),
                color = if (isRecording && !isPaused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(64.dp))

            // WAVEFORM
            WaveformVisualizer(
                amplitude = if (isPaused) 0.01f else amplitude,
                isRecording = isRecording && !isPaused,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // CONTROLS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MARK BUTTON
                SmallControl(
                    icon = Icons.Default.Flag,
                    label = "MARK",
                    enabled = isRecording,
                    onClick = { viewModel.addMarker() }
                )

                // MAIN RECORD/STOP BUTTON
                Box(contentAlignment = Alignment.Center) {
                    if (isRecording) {
                        Button(
                            onClick = { viewModel.stopRecording() },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(32.dp), tint = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED
                                
                                if (hasPermission) {
                                    selectedSpeakerId?.let { viewModel.startRecording(it, selectedCategory, activePromptTitle) }
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = selectedSpeakerId != null,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = "Record", modifier = Modifier.size(32.dp))
                        }
                    }
                }

                // PAUSE BUTTON
                SmallControl(
                    icon = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                    label = if (isPaused) "RESUME" else "PAUSE",
                    enabled = isRecording,
                    onClick = { viewModel.togglePause() }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (!isRecording && selectedSpeakerId == null) {
                Text(
                    "Please select or add a speaker first",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            // CANCEL BUTTON
            if (isRecording) {
                TextButton(
                    onClick = { viewModel.discardReview() /* Or a specific cancel method */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CANCEL RECORDING")
                }
            }
        }

        reviewRecording?.let { recording ->
            RecordingReviewScreen(
                recording = recording,
                onSave = { viewModel.saveReview() },
                onDiscard = { viewModel.discardReview() }
            )
        }
    }
}

@Composable
fun SmallControl(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline
        )
    }
}
