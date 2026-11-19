package com.example.fueltracker.util

import java.util.Locale

fun String.toDoubleFlexible(): Double? {
    val cleaned = this.trim().replace(',', '.')
    if (cleaned.isEmpty()) return null
    return cleaned.toDoubleOrNull()
}

fun Double.format(decimals: Int): String =
    String.format(Locale.US, "%.${decimals}f", this)

/** Убирает хвостовые нули: 0.0 -> "0", 9.0 -> "9", 2.34 -> "2,34". */
fun Double.formatSmart(maxDecimals: Int = 3): String {
    val rounded = String.format(Locale.US, "%.${maxDecimals}f", this)
    val trimmed = rounded.trimEnd('0').trimEnd('.')
    return if (trimmed.isEmpty()) "0" else trimmed.replace('.', ',')
}
