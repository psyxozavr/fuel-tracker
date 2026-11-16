package com.example.fueltracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "days",
    foreignKeys = [
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("carId")]
)
data class Day(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carId: Long,
    val epochDay: Long,        // дата в днях с 1970 (LocalDate.toEpochDay())
    val mileage: Double,       // пробег за день, км (ввод)
    val odometer: Double,      // одометр на конец дня (авто)
    val startFuel: Double,     // остаток на начало дня, л (авто)
    val refuel: Double,        // заправлено за день, л (ввод, 0 если не было)
    val consumed: Double,      // израсходовано, л (авто)
    val endFuel: Double        // остаток на конец дня, л (авто)
)
