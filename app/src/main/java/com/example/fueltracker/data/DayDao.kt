package com.example.fueltracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {
    @Query("SELECT * FROM days WHERE carId = :carId ORDER BY epochDay ASC")
    fun getAllForCar(carId: Long): Flow<List<Day>>

    @Query("SELECT * FROM days WHERE carId = :carId ORDER BY epochDay ASC")
    suspend fun getAllForCarSync(carId: Long): List<Day>

    @Query("SELECT * FROM days WHERE carId = :carId AND epochDay = :epochDay LIMIT 1")
    suspend fun getByDate(carId: Long, epochDay: Long): Day?

    @Query("SELECT * FROM days WHERE carId = :carId AND epochDay >= :fromEpochDay ORDER BY epochDay ASC")
    suspend fun getFrom(carId: Long, fromEpochDay: Long): List<Day>

    @Query("SELECT * FROM days WHERE carId = :carId AND epochDay BETWEEN :start AND :end ORDER BY epochDay ASC")
    fun getForMonth(carId: Long, start: Long, end: Long): Flow<List<Day>>

    @Insert
    suspend fun insert(day: Day): Long

    @Update
    suspend fun update(day: Day)

    @Delete
    suspend fun delete(day: Day)
}
