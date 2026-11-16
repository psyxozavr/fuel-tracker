package com.example.fueltracker.util

import java.util.Locale

/**
 * Парсит число из строки, понимая и точку, и запятую как разделитель.
 * Возвращает null, если строку нельзя превратить в число.
 */
fun String.toDoubleFlexible(): Double? {
    val cleaned = this.trim().replace(',', '.')
    if (cleaned.isEmpty()) return null
    return cleaned.toDoubleOrNull()
}

/** Форматирует число с заданным количеством знаков после запятой (точка). */
fun Double.format(decimals: Int): String =
    String.format(Locale.US, "%.${decimals}f", this)
