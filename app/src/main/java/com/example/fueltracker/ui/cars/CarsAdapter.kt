package com.example.fueltracker.ui.cars

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.Car

class CarsAdapter(
    private val onClick: (Car) -> Unit,
    private val onLongClick: (Car) -> Unit
) : RecyclerView.Adapter<CarsAdapter.VH>() {

    private val items = mutableListOf<Car>()

    fun submit(list: List<Car>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val car = items[position]
        holder.name.text = car.name
        holder.itemView.setOnClickListener { onClick(car) }
        holder.itemView.setOnLongClickListener {
            onLongClick(car)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tvCarName)
    }
}
