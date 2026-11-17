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

class DayAdapter(
    private val onClick: (Day) -> Unit
) : RecyclerView.Adapter<DayAdapter.VH>() {

    private val items = mutableListOf<Day>()
    private val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("ru"))

    fun submit(list: List<Day>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val d = items[position]
        val date = LocalDate.ofEpochDay(d.epochDay)
        holder.date.text = date.format(fmt)
        holder.details.text = String.format(
            Locale.getDefault(),
            "Одометр: %.0f км · Пробег: %.0f км · Заправ.: %.3f л · Расход: %.3f л · Кон.: %.3f л",
            d.odometer, d.mileage, d.refuel, d.consumed, d.endFuel
        )
        holder.itemView.setOnClickListener { onClick(d) }
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val date: TextView = v.findViewById(R.id.tvDate)
        val details: TextView = v.findViewById(R.id.tvDetails)
    }
}
