package com.example.fueltracker.ui.car

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.fueltracker.R
import com.example.fueltracker.data.Day
import com.example.fueltracker.data.FuelRepository
import com.example.fueltracker.util.toDoubleFlexible
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Показывает диалог добавления/редактирования дня.
 * Если [existing] != null — поля предзаполнены, дата фиксирована на этот день.
 * Иначе — дата по умолчанию «сегодня», пользователь может её поменять.
 */
fun showDayDialog(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    repo: FuelRepository,
    carId: Long,
    existing: Day?,
    onSaved: () -> Unit = {}
) {
    val view = android.view.LayoutInflater.from(context)
        .inflate(R.layout.dialog_day, null)

    val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
    val etMileage = view.findViewById<EditText>(R.id.etMileage)
    val etRefuel = view.findViewById<EditText>(R.id.etRefuel)

    val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("ru"))
    var selectedDate: LocalDate = if (existing != null) {
        LocalDate.ofEpochDay(existing.epochDay)
    } else {
        LocalDate.now()
    }

    fun updateDateButton() {
        btnPickDate.text = selectedDate.format(fmt)
    }
    updateDateButton()

    if (existing != null) {
        etMileage.setText(existing.mileage.toString())
        etRefuel.setText(existing.refuel.toString())
    }

    btnPickDate.setOnClickListener {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                updateDateButton()
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }

    val dialog = AlertDialog.Builder(context)
        .setTitle(if (existing != null) "Редактировать день" else "Добавить день")
        .setView(view)
        .setPositiveButton("Сохранить", null)
        .setNegativeButton("Отмена", null)
        .create()

    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val mileageText = etMileage.text.toString().trim()
            val refuelText = etRefuel.text.toString().trim()

            val mileage = mileageText.toDoubleFlexible()
            val refuel = if (refuelText.isEmpty()) 0.0 else refuelText.toDoubleFlexible()

            if (mileage == null || refuel == null) {
                Toast.makeText(context, "Введите корректный пробег", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleOwner.lifecycleScope.launch {
                repo.upsertDay(carId, selectedDate.toEpochDay(), mileage, refuel)
                onSaved()
            }
            dialog.dismiss()
        }
    }
    dialog.show()
}
