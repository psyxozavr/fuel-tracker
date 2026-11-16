package com.example.fueltracker.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.Day
import com.example.fueltracker.data.FuelRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class MonthFragment : Fragment() {

    private var carId: Long = 0
    private lateinit var repo: FuelRepository
    private lateinit var adapter: MonthAdapter
    private var current: YearMonth = YearMonth.now()
    private var currentDays: List<Day> = emptyList()

    companion object {
        fun newInstance(carId: Long) = MonthFragment().apply {
            arguments = Bundle().apply { putLong("carId", carId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View =
        inflater.inflate(R.layout.fragment_month, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        carId = requireArguments().getLong("carId")
        val db = AppDatabase.get(requireContext())
        repo = FuelRepository(db.carDao(), db.dayDao())
        adapter = MonthAdapter()

        view.findViewById<RecyclerView>(R.id.recyclerMonth).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MonthFragment.adapter
        }

        view.findViewById<Button>(R.id.btnPrevMonth).setOnClickListener {
            current = current.minusMonths(1); loadMonth()
        }
        view.findViewById<Button>(R.id.btnNextMonth).setOnClickListener {
            current = current.plusMonths(1); loadMonth()
        }

        loadMonth()
    }

    private fun loadMonth() {
        val title = view?.findViewById<TextView>(R.id.tvMonthTitle) ?: return
        val fmt = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))
        title.text = current.atDay(1).format(fmt).replaceFirstChar { it.uppercase() }

        val start = current.atDay(1).toEpochDay()
        val end = current.atEndOfMonth().toEpochDay()

        viewLifecycleOwner.lifecycleScope.launch {
            repo.getMonthDays(carId, start, end).collect { days ->
                currentDays = days
                adapter.submit(days)
                updateTotals(days)
            }
        }
    }

    private fun updateTotals(days: List<Day>) {
        val tv = view?.findViewById<TextView>(R.id.tvTotals) ?: return
        if (days.isEmpty()) {
            tv.text = "Нет записей за этот месяц"
            return
        }
        val totalMileage = days.sumOf { it.mileage }
        val totalRefuel = days.sumOf { it.refuel }
        val totalConsumed = days.sumOf { it.consumed }
        val endFuel = days.last().endFuel
        val avg = if (totalMileage > 0) totalConsumed / totalMileage * 100 else 0.0
        tv.text = String.format(
            Locale.getDefault(),
            "Итого: пробег %.0f км · заправлено %.1f л · израсходовано %.1f л · остаток %.1f л · ср. %.2f л/100",
            totalMileage, totalRefuel, totalConsumed, endFuel, avg
        )
    }
}
