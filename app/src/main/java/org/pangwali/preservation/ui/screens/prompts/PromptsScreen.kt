package org.pangwali.preservation.ui.screens.prompts

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.pangwali.preservation.data.db.PromptEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecording: (String, String) -> Unit,
    viewModel: PromptsViewModel
) {
    val context = LocalContext.current
    val prompts by viewModel.prompts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val csvPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.importCsv(context, it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Translation Data Prompts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { csvPickerLauncher.launch("text/*") }) {
                        Icon(Icons.Default.Upload, contentDescription = "Import CSV")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Prompt")
            }
        }
    ) { paddingValues ->
        if (prompts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No prompts available", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { csvPickerLauncher.launch("text/*") }) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("IMPORT CSV")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(prompts) { prompt ->
                    PromptItem(
                        prompt = prompt,
                        onRecord = { onNavigateToRecording(prompt.id, prompt.hindiText) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddPromptDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { hindi, category ->
                    viewModel.addPrompt(hindi, category)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PromptItem(prompt: PromptEntity, onRecord: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prompt.hindiText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = prompt.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onRecord) {
                Icon(Icons.Default.Mic, contentDescription = "Record Response", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun AddPromptDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var hindi by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Translation Prompt") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = hindi, onValueChange = { hindi = it }, label = { Text("Hindi Prompt") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(hindi, category) }, enabled = hindi.isNotBlank()) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        }
    )
}
