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
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.Order
import com.example.cleaberservice.models.OrderAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale

class CleanerMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cleaner_main, container, false)
    }

    private lateinit var adapter: OrderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val constraint = view.findViewById<LinearLayout>(R.id.CleanerMainFragmentLLViewContainer)
        val orders = view.findViewById<RecyclerView>(R.id.CleanerMainFragmentRVOrders)
        val bOrdersList = view.findViewById<Button>(R.id.CleanerMainFragmentBNavOrders)

        orders.setHasFixedSize(true)
        orders.layoutManager = LinearLayoutManager(view.context)

        val activeOrdersList: MutableMap<String, Order> = mutableMapOf()
        DB.users[DB.auth.currentUser!!.uid]!!.orders.keys.forEach {
            if(DB.orders[it]?.status == false)
                activeOrdersList[it] = DB.orders[it]!!
        }
        adapter = OrderAdapter(requireContext(), activeOrdersList, navController)
        orders.adapter = adapter
        if(activeOrdersList.isNotEmpty()) {
            constraint.visibility = View.GONE
            orders.visibility = View.VISIBLE
        }

        bOrdersList.setOnClickListener {
            navController.navigate(R.id.cleanerOrdersList)
        }
    }
}