package org.pangwali.preservation.ui.screens.wordlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.pangwali.preservation.data.db.WordlistEntity

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordlistScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecording: (String) -> Unit,
    viewModel: WordlistViewModel
) {
    val context = LocalContext.current
    val wordlist by viewModel.wordlist.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importCsv(context, it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Wordlist Mode") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { csvPickerLauncher.launch("text/*") }) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Import CSV")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (wordlist.isNotEmpty()) {
            val currentWord = wordlist[currentIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // PROGRESS
                Text(
                    text = "WORD ${currentIndex + 1} / ${wordlist.size}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // WORD DISPLAY
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Hindi Concept",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentWord.hindiWord,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // RECORD BUTTON
                Button(
                    onClick = { onNavigateToRecording(currentWord.id) },
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Record", modifier = Modifier.size(32.dp))
                }

                // NAVIGATION
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousWord() },
                        enabled = currentIndex > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                    }
                    
                    TextButton(onClick = { /* Skip logic */ }) {
                        Text("SKIP")
                    }
                    
                    IconButton(
                        onClick = { viewModel.nextWord() },
                        enabled = currentIndex < wordlist.size - 1
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No words in list", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Import a CSV file with one Hindi word per line.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { csvPickerLauncher.launch("text/*") }) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IMPORT CSV")
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = { viewModel.seedInitialWords() }) {
                    Text("LOAD DEFAULTS")
                }
            }
        }
    }
}
