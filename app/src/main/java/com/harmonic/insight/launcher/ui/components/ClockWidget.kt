package com.harmonic.insight.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            // Update every second
            delay(1000L)
        }
    }

    val date = remember(currentTime / 60000) { Date(currentTime) }

    val dateFormat = remember { SimpleDateFormat("M月d日(E)", Locale.JAPANESE) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.JAPANESE) }

    val dateString = remember(currentTime / 60000) { dateFormat.format(date) }
    val timeString = remember(currentTime / 60000) { timeFormat.format(date) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = dateString,
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
        )
        Text(
            text = timeString,
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            color = textColor,
            lineHeight = 72.sp,
        )
    }
}
