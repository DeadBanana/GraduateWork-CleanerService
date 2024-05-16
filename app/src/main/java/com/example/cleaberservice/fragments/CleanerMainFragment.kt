package com.example.cleaberservice.fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.OrderAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date
import java.util.Locale
import kotlin.math.log

class CleanerMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cleaner_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: FloatingActionButton = view.findViewById(R.id.CleanerMainFragmentFBFilter)
        val lvOrders = view.findViewById<ListView>(R.id.CleanerMainFragmentLVOrders)

        var selectedDateBefore: Date
        var selectedDateAfter: Date
        var selectedServiceId: String
        var sortedOrders = DB.orders.filter { x -> !x.value.status }
        var orderAdapter = OrderAdapter(view.context, sortedOrders)
        lvOrders.adapter = orderAdapter

        fab.setOnClickListener { view ->
            val dialogView = LayoutInflater.from(view.context).inflate(R.layout.filter_dialog, null)
            val dateBefore = dialogView.findViewById<EditText>(R.id.filterDialogDateBefore)
            val dateAfter = dialogView.findViewById<EditText>(R.id.filterDialogDateAfter)
            val servicesSpinner = dialogView.findViewById<Spinner>(R.id.filterDialogServiceSpinner)

            dateBefore.setOnClickListener {
                selectedDateBefore = showDateDialog(view)
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = format.format(selectedDateBefore)
                dateBefore.setText(formattedDate)
            }
            dateAfter.setOnClickListener {
                selectedDateAfter = showDateDialog(view)
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = format.format(selectedDateAfter)
                dateAfter.setText(formattedDate)
            }

            val services = DB.services.values.toList()
            val servicesNames = services.map { it.name }
            val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, servicesNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            servicesSpinner.adapter = adapter

            val alertDialog = AlertDialog.Builder(view.context)
                .setView(dialogView)
                .setTitle("Фильтр")
                .setNegativeButton("Сбросить") {dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Применить") {dialog, _ ->

                    selectedServiceId = services[servicesSpinner.selectedItemPosition].id
                    Log.d("MyLog", "Selected service id:${selectedServiceId}<CleanerMainFrame>")
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }
    }

    fun showDateDialog(view: View): Date {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)
        var date: Date = now.time

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            date = selectedDate.time
            Log.d("MyLog", "Selected date: $date")
        }
        DatePickerDialog(view.context, dateSetListener, year, month, day).show()

        return date
    }
}