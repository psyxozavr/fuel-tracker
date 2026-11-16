package com.example.fueltracker.ui.car

import com.example.fueltracker.util.toDoubleFlexible
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.FuelRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.time.LocalDate

class DayFragment : Fragment() {

    private var carId: Long = 0
    private lateinit var repo: FuelRepository
    private lateinit var adapter: DayAdapter

    companion object {
        fun newInstance(carId: Long) = DayFragment().apply {
            arguments = Bundle().apply { putLong("carId", carId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_day, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        carId = requireArguments().getLong("carId")
        val db = AppDatabase.get(requireContext())
        repo = FuelRepository(db.carDao(), db.dayDao())
        adapter = DayAdapter()

        view.findViewById<RecyclerView>(R.id.recyclerDays).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DayFragment.adapter
        }

        view.findViewById<FloatingActionButton>(R.id.fabAddDay).setOnClickListener {
            showAddDialog()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repo.getDays(carId).collect { adapter.submit(it) }
        }
    }

    private fun showAddDialog() {
        val v = layoutInflater.inflate(R.layout.dialog_day, null)
        val etMileage = v.findViewById<EditText>(R.id.etMileage)
        val etRefuel = v.findViewById<EditText>(R.id.etRefuel)

        val today = LocalDate.now()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить день")
            .setView(v)
            .setPositiveButton("Сохранить", null)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val mileage = etMileage.text.toString().toDoubleFlexible() ?: 0.0
                val refuel = etRefuel.text.toString().toDoubleFlexible() ?: 0.0
                viewLifecycleOwner.lifecycleScope.launch {
                    repo.upsertDay(carId, today.toEpochDay(), mileage, refuel)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
