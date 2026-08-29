package org.pangwali.preservation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun WaveformVisualizer(
    amplitude: Float,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val barCount = 40
    val amplitudes = remember { mutableStateListOf<Float>().apply { repeat(barCount) { add(0.01f) } } }

    LaunchedEffect(amplitude) {
        if (isRecording) {
            amplitudes.removeAt(0)
            amplitudes.add(amplitude.coerceIn(0.01f, 1f))
        }
    }

    val color = if (isRecording) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val width = size.width
        val height = size.height
        val barWidth = width / (barCount * 1.5f)
        val space = barWidth * 0.5f

        amplitudes.forEachIndexed { index, amp ->
            val barHeight = amp * height
            val x = index * (barWidth + space)
            val y = (height - barHeight) / 2
            
            drawRect(
                color = color,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }
}
