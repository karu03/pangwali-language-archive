package org.pangwali.preservation.ui.screens.recording

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptRecordingScreen(
    onNavigateBack: () -> Unit,
    viewModel: PromptRecordingViewModel
) {
    val currentIndex by viewModel.currentIndex.collectAsState()
    val prompts by viewModel.prompts.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Elicitation Prompts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (prompts.isNotEmpty()) {
            val currentPrompt = prompts[currentIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Main Cue Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(currentPrompt.category.uppercase(), style = MaterialTheme.typography.labelSmall)
                        Text(currentPrompt.hindiText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        HorizontalDivider()
                        Text("Concept: " + (currentPrompt.expectedSachConcept ?: "N/A"), style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.previousPrompt() }, enabled = currentIndex > 0) {
                        Text("← Previous")
                    }
                    Text("${currentIndex + 1} / ${prompts.size}")
                    TextButton(onClick = { viewModel.nextPrompt() }, enabled = currentIndex < prompts.size - 1) {
                        Text("Next →")
                    }
                }
                
                // Record Button
                Button(
                    onClick = { /* Navigate to recorder with this prompt */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("RECORD RESPONSE")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
