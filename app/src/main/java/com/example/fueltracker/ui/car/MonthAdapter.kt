package com.example.fueltracker.ui.car

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.Day
import com.example.fueltracker.util.colorForEpochDay
import com.example.fueltracker.util.formatSmart
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
        holder.odometer.text = d.odometer.formatSmart()
        holder.mileage.text = d.mileage.formatSmart()
        holder.startFuel.text = d.startFuel.formatSmart()
        holder.refuel.text = d.refuel.formatSmart()
        holder.consumed.text = d.consumed.formatSmart()
        holder.endFuel.text = d.endFuel.formatSmart()
        val colorRes = colorForEpochDay(d.epochDay)
        holder.root.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorRes))
        val textViews = listOf(holder.date, holder.odometer, holder.mileage,
            holder.startFuel, holder.refuel, holder.consumed, holder.endFuel)
        textViews.forEach { it.setTextColor(Color.BLACK) }
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val root: LinearLayout = v.findViewById(R.id.rootMonthRow)
        val date: TextView = v.findViewById(R.id.tvDate)
        val odometer: TextView = v.findViewById(R.id.tvOdometer)
        val mileage: TextView = v.findViewById(R.id.tvMileage)
        val startFuel: TextView = v.findViewById(R.id.tvStartFuel)
        val refuel: TextView = v.findViewById(R.id.tvRefuel)
        val consumed: TextView = v.findViewById(R.id.tvConsumed)
        val endFuel: TextView = v.findViewById(R.id.tvEndFuel)
    }
}
