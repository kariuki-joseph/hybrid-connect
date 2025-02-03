package com.example.hybridconnect.domain.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatTimeAgo(timeInMillis: Long): String {
    val now = System.currentTimeMillis()
    val durationInMillis = now - timeInMillis

    return when {
        durationInMillis < TimeUnit.MINUTES.toMillis(1) -> "Just now"
        durationInMillis < TimeUnit.HOURS.toMillis(1) -> "${
            TimeUnit.MILLISECONDS.toMinutes(
                durationInMillis
            )
        }min ago"

        durationInMillis < TimeUnit.DAYS.toMillis(1) -> {
            val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60
            "${hours}h ${minutes}min ago"
        }

        else -> {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = calendar.get(Calendar.DAY_OF_YEAR)

            calendar.timeInMillis = timeInMillis
            val transactionDay = calendar.get(Calendar.DAY_OF_YEAR)

            if (transactionDay == yesterday) {
                val date = Date(timeInMillis)
                val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                "Yesterday, ${formatter.format(date)}"

            } else {
                val date = Date(timeInMillis)
                val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                formatter.format(date)
            }
        }
    }
}

fun formatTime(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(date)
}

fun formatDateTime(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

fun todayRange(): LongRange {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfDay = calendar.timeInMillis

    return startOfDay..endOfDay
}