package com.example.fueltracker.ui.car

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fueltracker.R
import com.example.fueltracker.data.AppDatabase
import com.example.fueltracker.data.FuelRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

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

        adapter = DayAdapter { day ->
            showDayDialog(
                context = requireContext(),
                lifecycleOwner = viewLifecycleOwner,
                repo = repo,
                carId = carId,
                existing = day
            )
        }

        view.findViewById<RecyclerView>(R.id.recyclerDays).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DayFragment.adapter
        }

        view.findViewById<FloatingActionButton>(R.id.fabAddDay).setOnClickListener {
            showDayDialog(
                context = requireContext(),
                lifecycleOwner = viewLifecycleOwner,
                repo = repo,
                carId = carId,
                existing = null
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repo.getDays(carId).collect { adapter.submit(it) }
        }
    }
}
