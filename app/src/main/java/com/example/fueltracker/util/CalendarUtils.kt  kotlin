package com.example.fueltracker.util

import androidx.annotation.ColorRes
import com.example.fueltracker.R
import java.time.DayOfWeek
import java.time.LocalDate

@ColorRes
fun colorForDate(date: LocalDate): Int {
    return when (date.dayOfWeek) {
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> R.color.day_weekend
        else -> R.color.day_working
    }
}

@ColorRes
fun colorForEpochDay(epochDay: Long): Int = colorForDate(LocalDate.ofEpochDay(epochDay))
