package com.example.myapplication.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TimeColorUtils {
    
    @Composable
    fun getColorForTimeRemaining(endTime: Long?): Color {
        if (endTime == null) return MaterialTheme.colorScheme.surfaceVariant
        
        val currentTime = System.currentTimeMillis()
        val timeLeft = endTime - currentTime
        
        return when {
            timeLeft < 0 -> Color(0xFFD32F2F) // Red (Overdue)
            timeLeft < 24 * 60 * 60 * 1000 -> Color(0xFFFF5252) // Light Red (Less than 24h)
            timeLeft < 3 * 24 * 60 * 60 * 1000 -> Color(0xFFFFD740) // Amber/Yellow (Less than 3 days)
            else -> Color(0xFF66BB6A) // Green (Plenty of time)
        }
    }
}
