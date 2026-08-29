package org.pangwali.preservation.ui.screens.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PangiValleyMap(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pangi Valley Map") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw simple "river" (Chenab)
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, canvasHeight * 0.5f),
                    end = Offset(canvasWidth, canvasHeight * 0.4f),
                    strokeWidth = 5f
                )

                // Draw villages as nodes
                val villages = listOf(
                    Offset(canvasWidth * 0.2f, canvasHeight * 0.3f), // Sach
                    Offset(canvasWidth * 0.5f, canvasHeight * 0.45f), // Killar
                    Offset(canvasWidth * 0.8f, canvasHeight * 0.6f)  // Dharwas
                )

                villages.forEach { pos ->
                    drawCircle(color = Color.Red, radius = 20f, center = pos)
                }
            }
        }
    }
}
