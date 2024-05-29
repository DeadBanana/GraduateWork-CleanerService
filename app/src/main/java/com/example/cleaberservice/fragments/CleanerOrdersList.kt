package com.example.cleaberservice.fragments

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.OrderAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class CleanerOrdersList : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cleaner_orders_list, container, false)
    }

    private val sortedOrders = DB.orders.filter { x -> x.value.visibility }.toMutableMap()
    private lateinit var orderAdapter: OrderAdapter

    override fun onResume() {
        super.onResume()
//        val filteredOrders = DB.orders.filter { x -> x.value.visibility }.toMutableMap()
//        sortedOrders.clear()
//        sortedOrders.putAll(filteredOrders)
        orderAdapter.updateKeys()
        orderAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab: FloatingActionButton = view.findViewById(R.id.CleanerOrdersListFragmentFBFilter)
        val lvOrders = view.findViewById<RecyclerView>(R.id.CleanerOrdersListFragmentLVOrders)
        val navController = NavHostFragment.findNavController(this)

        val defaultStartDate: Long = 631152000000 //01.01.1990
        val defaultEndDate: Long = 4733856000000 //01.01.2120
        var selectedDateBefore = defaultStartDate
        var selectedDateAfter = defaultEndDate
        var selectedServiceId: String? = null
        orderAdapter = OrderAdapter(view.context, sortedOrders, navController, true)
        lvOrders.setHasFixedSize(true)
        lvOrders.layoutManager = LinearLayoutManager(view.context)
        lvOrders.adapter = orderAdapter

        fab.setOnClickListener { _ ->
            val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_filter, null)
            val dateBefore = dialogView.findViewById<EditText>(R.id.filterDialogDateBefore)
            val dateAfter = dialogView.findViewById<EditText>(R.id.filterDialogDateAfter)
            val servicesSpinner = dialogView.findViewById<Spinner>(R.id.filterDialogServiceSpinner)

            if(selectedDateBefore != defaultStartDate) {
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = format.format(selectedDateBefore)
                dateBefore.setText(formattedDate)
            }

            if(selectedDateAfter != defaultEndDate) {
                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = format.format(selectedDateAfter)
                dateAfter.setText(formattedDate)
            }

            dateBefore.setOnClickListener {
                showDateDialog(view, dateBefore) { date ->
                    selectedDateBefore = date
                }
            }
            dateAfter.setOnClickListener {
                showDateDialog(view, dateAfter) { date ->
                    selectedDateAfter = date
                }
            }

            val services = DB.services.values.toList()
            val servicesNames = mutableListOf("Все услуги")
            servicesNames.addAll(services.map{ it.name })
            val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, servicesNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            servicesSpinner.adapter = adapter

            if(selectedServiceId != null) {
                servicesSpinner.setSelection(services.indexOf(services.first { x -> x.id == selectedServiceId }) + 1)
            }

            val alertDialog = AlertDialog.Builder(view.context)
                .setView(dialogView)
                .setTitle("Фильтр")
                .setNegativeButton("Сбросить") {dialog, _ ->
                    val filteredOrders = DB.orders.filter { x -> x.value.visibility }.toMutableMap()
                    selectedDateBefore = defaultStartDate
                    selectedDateAfter = defaultEndDate
                    selectedServiceId = null
                    sortedOrders.clear()
                    sortedOrders.putAll(filteredOrders)
                    orderAdapter.updateKeys()
                    orderAdapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setPositiveButton("Применить") {dialog, _ ->
                    selectedServiceId = if (servicesSpinner.selectedItemPosition != 0) {
                        services[servicesSpinner.selectedItemPosition - 1].id
                    }
                    else
                        null
                    val filteredOrders = DB.orders.filter { x ->
                        (selectedServiceId == null || x.value.services[selectedServiceId] != null)
                                && x.value.date >= selectedDateBefore
                                && x.value.date <= selectedDateAfter
                                && x.value.visibility}.toMutableMap()
                    sortedOrders.clear()
                    sortedOrders.putAll(filteredOrders)
                    orderAdapter.updateKeys()
                    orderAdapter.notifyDataSetChanged()
                    Log.d("MyLog", "Selected service id:${selectedServiceId}<CleanerMainFrame>")
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }
    }

    fun showDateDialog(view: View, editText: EditText, callback: (Long) -> Unit) {
        val now = Calendar.getInstance()
        val year = now.get(Calendar.YEAR)
        val month = now.get(Calendar.MONTH)
        val day = now.get(Calendar.DAY_OF_MONTH)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, selectedYear)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val date = selectedDate.time
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val formattedDate = format.format(date)
            editText.setText(formattedDate)
            Log.d("MyLog", "Selected date: $date")
            callback(date.time)
        }
        DatePickerDialog(view.context, dateSetListener, year, month, day).show()
    }
}