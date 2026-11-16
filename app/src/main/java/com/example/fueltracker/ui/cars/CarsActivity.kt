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

        adapter = CarsAdapter { car ->
            val i = Intent(this, CarActivity::class.java)
            i.putExtra("carId", car.id)
            startActivity(i)
        }

        findViewById<RecyclerView>(R.id.recyclerCars).apply {
            layoutManager = LinearLayoutManager(this@CarsActivity)
            adapter = this@CarsActivity.adapter
        }

        findViewById<FloatingActionButton>(R.id.fabAddCar).setOnClickListener {
            showCarDialog()
        }

        lifecycleScope.launch {
            repo.getCars().collect { adapter.submit(it) }
        }
    }

    private fun showCarDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_car, null)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etNorm = view.findViewById<EditText>(R.id.etNorm)
        val etOdo = view.findViewById<EditText>(R.id.etInitialOdometer)
        val etFuel = view.findViewById<EditText>(R.id.etInitialFuel)

        AlertDialog.Builder(this)
            .setTitle("Новая машина")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                val norm = etNorm.text.toString().toDoubleOrNull() ?: 8.0
                val odo = etOdo.text.toString().toDoubleOrNull() ?: 0.0
                val fuel = etFuel.text.toString().toDoubleOrNull() ?: 0.0
                lifecycleScope.launch {
                    repo.addCar(Car(name = name, normLPer100 = norm,
                        initialOdometer = odo, initialFuel = fuel))
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
