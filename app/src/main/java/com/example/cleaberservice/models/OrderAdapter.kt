package com.example.cleaberservice.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.cleaberservice.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(context: Context, private val orders: Map<String, Order>) : BaseAdapter() {
    private var keys =  orders.keys.toTypedArray()

    fun updateKeys() {
        keys =  orders.keys.toTypedArray()
    }

    override fun getCount(): Int {
        return orders.size
    }

    override fun getItem(position: Int): Order? {
        return orders[keys[position]]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?:
        LayoutInflater.from(parent.context).inflate(R.layout.list_order, parent, false)

        val tvDate: TextView = view.findViewById(R.id.listOrderDate)
        val tvAddress: TextView = view.findViewById(R.id.listOrderAddress)
        val tvStatus: TextView = view.findViewById(R.id.listOrderStatus)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
//        tvDate.text = formatter.format(getItem(position)?.date)
        val orderDateLong = getItem(position)?.date
        if (orderDateLong is Long) {
            val orderDate = Date(orderDateLong)
            tvDate.text = formatter.format(orderDate)
        } else {
            Log.d("MyLog", "Invalid date: $orderDateLong")
        }
        tvAddress.text = getItem(position)?.address
        tvStatus.text = getItem(position)?.status.toString()

        return view
    }
}