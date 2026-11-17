package com.example.fueltracker.ui.cars

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.Car
import com.example.fueltracker.data.FuelRepository
import com.example.fueltracker.ui.car.CarActivity
import com.example.fueltracker.util.toDoubleFlexible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CarsActivity : AppCompatActivity() {

    private lateinit var repo: FuelRepository
    private lateinit var adapter: CarsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cars)

        val db = AppDatabase.get(this)
        repo = FuelRepository(db.carDao(), db.dayDao())

        adapter = CarsAdapter(
            onClick = { car ->
                val i = Intent(this, CarActivity::class.java)
                i.putExtra("carId", car.id)
                startActivity(i)
            },
            onLongClick = { car -> showCarActions(car) }
        )

        findViewById<RecyclerView>(R.id.recyclerCars).apply {
            layoutManager = LinearLayoutManager(this@CarsActivity)
            adapter = this@CarsActivity.adapter
        }

        findViewById<FloatingActionButton>(R.id.fabAddCar).setOnClickListener {
            showCarDialog(null)
        }

        lifecycleScope.launch {
            repo.getCars().collect { adapter.submit(it) }
        }
    }

    /** Долгое нажатие: выбор действия над машиной. */
    private fun showCarActions(car: Car) {
        AlertDialog.Builder(this)
            .setTitle(car.name)
            .setItems(arrayOf("Редактировать", "Удалить")) { _, which ->
                when (which) {
                    0 -> showCarDialog(car)
                    1 -> confirmDelete(car)
                }
            }
            .show()
    }

    /** Диалог создания (car == null) или редактирования (car != null). */
    private fun showCarDialog(car: Car?) {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etNorm = view.findViewById<EditText>(R.id.etNorm)
        val etOdo = view.findViewById<EditText>(R.id.etInitialOdometer)
        val etFuel = view.findViewById<EditText>(R.id.etInitialFuel)

        if (car != null) {
            etName.setText(car.name)
            etNorm.setText(car.normLPer100.toString())
            etOdo.setText(car.initialOdometer.toString())
            etFuel.setText(car.initialFuel.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (car == null) "Новая машина" else "Редактировать машину")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                val norm = etNorm.text.toString().toDoubleFlexible() ?: 8.0
                val odo = etOdo.text.toString().toDoubleFlexible() ?: 0.0
                val fuel = etFuel.text.toString().toDoubleFlexible() ?: 0.0

                lifecycleScope.launch {
                    if (car == null) {
                        repo.addCar(Car(name = name, normLPer100 = norm,
                            initialOdometer = odo, initialFuel = fuel))
                    } else {
                        repo.updateCar(car.copy(
                            name = name,
                            normLPer100 = norm,
                            initialOdometer = odo,
                            initialFuel = fuel
                        ))
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun confirmDelete(car: Car) {
        AlertDialog.Builder(this)
            .setTitle("Удалить машину?")
            .setMessage("Все дни и записи по этой машине тоже будут удалены. Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch { repo.deleteCar(car) }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
