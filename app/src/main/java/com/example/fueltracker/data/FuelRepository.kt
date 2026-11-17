package com.example.fueltracker.data

import kotlinx.coroutines.flow.Flow

class FuelRepository(
    private val carDao: CarDao,
    private val dayDao: DayDao
) {
    fun getCars(): Flow<List<Car>> = carDao.getAll()
    suspend fun getCar(id: Long): Car? = carDao.getById(id)
    suspend fun addCar(car: Car): Long = carDao.insert(car)
    suspend fun updateCar(car: Car) = carDao.update(car)
    suspend fun deleteCar(car: Car) = carDao.delete(car)

    fun getDays(carId: Long): Flow<List<Day>> = dayDao.getAllForCar(carId)
    fun getMonthDays(carId: Long, start: Long, end: Long): Flow<List<Day>> =
        dayDao.getForMonth(carId, start, end)

    suspend fun upsertDay(carId: Long, epochDay: Long, mileage: Double, refuel: Double) {
        val car = carDao.getById(carId) ?: return
        val existing = dayDao.getByDate(carId, epochDay)

        val all = dayDao.getAllForCarSync(carId).sortedBy { it.epochDay }
        val previous = all.lastOrNull { it.epochDay < epochDay }

        if (previous != null && epochDay - previous.epochDay > 1) {
            var gapDay = previous.epochDay + 1
            while (gapDay < epochDay) {
                if (dayDao.getByDate(carId, gapDay) == null) {
                    dayDao.insert(
                        Day(
                            carId = carId,
                            epochDay = gapDay,
                            mileage = 0.0,
                            odometer = 0.0,
                            startFuel = 0.0,
                            refuel = 0.0,
                            consumed = 0.0,
                            endFuel = 0.0
                        )
                    )
                }
                gapDay++
            }
        }

        if (existing != null) {
            dayDao.update(existing.copy(mileage = mileage, refuel = refuel))
        } else {
            dayDao.insert(
                Day(
                    carId = carId,
                    epochDay = epochDay,
                    mileage = mileage,
                    odometer = 0.0,
                    startFuel = 0.0,
                    refuel = refuel,
                    consumed = 0.0,
                    endFuel = 0.0
                )
            )
        }

        val recalcFrom = if (previous != null) previous.epochDay + 1 else epochDay
        recalculateFrom(car, recalcFrom)
    }

    suspend fun deleteDay(carId: Long, epochDay: Long) {
        val car = carDao.getById(carId) ?: return
        val day = dayDao.getByDate(carId, epochDay) ?: return
        dayDao.delete(day)
        recalculateFrom(car, epochDay)
    }

    suspend fun recalculateAll(car: Car) {
        val all = dayDao.getAllForCarSync(car.id)
        if (all.isEmpty()) return
        val earliest = all.minOf { it.epochDay }
        recalculateFrom(car, earliest)
    }

    private suspend fun recalculateFrom(car: Car, fromEpochDay: Long) {
        val all = dayDao.getAllForCarSync(car.id).sortedBy { it.epochDay }

        var prevOdometer = car.initialOdometer
        var prevFuel = car.initialFuel

        for (d in all) {
            if (d.epochDay < fromEpochDay) {
                prevOdometer = d.odometer
                prevFuel = d.endFuel
            }
        }

        val startIndex = all.indexOfFirst { it.epochDay >= fromEpochDay }
        if (startIndex < 0) return

        for (i in startIndex until all.size) {
            val d = all[i]
            val odometer = prevOdometer + d.mileage
            val consumed = d.mileage * car.normLPer100 / 100.0
            val startFuel = prevFuel
            val endFuel = startFuel + d.refuel - consumed

            dayDao.update(d.copy(
                odometer = odometer,
                startFuel = startFuel,
                consumed = consumed,
                endFuel = endFuel
            ))

            prevOdometer = odometer
            prevFuel = endFuel
        }
    }
}
