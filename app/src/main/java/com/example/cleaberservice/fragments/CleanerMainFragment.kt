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

class CleanerMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cleaner_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        val bOrdersList = view.findViewById<Button>(R.id.CleanerMainFragmentBNavOrders)
        val bHistory = view.findViewById<Button>(R.id.CleanerMainFragmentBNavHistory)

        bOrdersList.setOnClickListener {
            navController.navigate(R.id.cleanerOrdersList)
        }

        bHistory.setOnClickListener {
            navController.navigate(R.id.historyListFragment2)
        }
    }
}