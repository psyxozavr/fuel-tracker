package com.example.fueltracker.ui.car

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MonthAdapter : RecyclerView.Adapter<MonthAdapter.VH>() {

    private val items = mutableListOf<Day>()
    private val fmt = DateTimeFormatter.ofPattern("dd MMM", Locale("ru"))

    fun submit(list: List<Day>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_month_row, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val d = items[position]
        holder.date.text = LocalDate.ofEpochDay(d.epochDay).format(fmt)
        holder.odometer.text = String.format(Locale.getDefault(), "%.0f", d.odometer)
        holder.mileage.text = String.format(Locale.getDefault(), "%.0f", d.mileage)
        holder.startFuel.text = String.format(Locale.getDefault(), "%.3f", d.startFuel)
holder.refuel.text = String.format(Locale.getDefault(), "%.3f", d.refuel)
holder.consumed.text = String.format(Locale.getDefault(), "%.3f", d.consumed)
holder.endFuel.text = String.format(Locale.getDefault(), "%.3f", d.endFuel)
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView = v.findViewById(R.id.tvDate)
        val odometer: TextView = v.findViewById(R.id.tvOdometer)
        val mileage: TextView = v.findViewById(R.id.tvMileage)
        val startFuel: TextView = v.findViewById(R.id.tvStartFuel)
        val refuel: TextView = v.findViewById(R.id.tvRefuel)
        val consumed: TextView = v.findViewById(R.id.tvConsumed)
        val endFuel: TextView = v.findViewById(R.id.tvEndFuel)
    }
}
