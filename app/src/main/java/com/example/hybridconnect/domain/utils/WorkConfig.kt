package com.example.hybridconnect.domain.utils

import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object WorkConfig {
    // Returns the initial delay in milliseconds based on a specific date
    fun delayForDate(year: Int, month: Int, day: Int): Long {
        val now = Calendar.getInstance()
        val targetDate = Calendar.getInstance().apply {
            set(year, month - 1, day, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return targetDate.timeInMillis - now.timeInMillis
    }

    // Returns the initial delay in milliseconds based on a specific time of day
    fun delayForTime(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return targetTime.timeInMillis - now.timeInMillis
    }

    // Converts a repeat interval to milliseconds
    fun repeatInterval(interval: Long, unit: TimeUnit): Long {
        return unit.toMillis(interval)
    }
}
