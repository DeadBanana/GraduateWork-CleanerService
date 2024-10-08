package com.example.cleaberservice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.Order
import com.example.cleaberservice.models.OrderAdapter

class HistoryListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_list, container, false)
    }

    lateinit var lvHistory: RecyclerView
    lateinit var adapter: OrderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)
        DB.addAdapter { adapter.updateKeys() }
        lvHistory = view.findViewById(R.id.HistoryListFragmentLVHistory)
        val tvMessage = view.findViewById<TextView>(R.id.HistoryListFragmentTVMessage)
        val orders: MutableMap<String, Order> = mutableMapOf()
        val currentUser = DB.users[DB.auth.currentUser!!.uid]!!
        currentUser.orders.keys.forEach {
            if(DB.orders[it]?.status == true)
                orders[it] = DB.orders[it]!!
        }
        if(orders.isNotEmpty()) {
            tvMessage.visibility = View.GONE
            lvHistory.visibility = View.VISIBLE
        }
        adapter = OrderAdapter(view.context, orders, navController, currentUser.role == 0)
        lvHistory.setHasFixedSize(true)
        lvHistory.layoutManager = LinearLayoutManager(view.context)
        lvHistory.adapter = adapter
    }
}