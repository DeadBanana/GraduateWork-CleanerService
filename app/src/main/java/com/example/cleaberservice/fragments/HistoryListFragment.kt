package com.example.cleaberservice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
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

    lateinit var lvHistory: ListView
    lateinit var adapter: OrderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DB.addAdapter { adapter.updateKeys() }
        lvHistory = view.findViewById(R.id.HistoryListFragmentLVHistory)
        val orders: MutableMap<String, Order> = mutableMapOf()
        DB.users[DB.auth.currentUser!!.uid]!!.orders.keys.forEach {
            orders[it] = DB.orders[it]!!
        }
        adapter = OrderAdapter(view.context, orders)
        lvHistory.adapter = adapter
    }
}