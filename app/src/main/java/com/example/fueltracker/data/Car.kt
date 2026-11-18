package com.example.fueltracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val normLPer100: Double,
    val winterNormLPer100: Double,
    val initialOdometer: Double,
    val initialFuel: Double
)
