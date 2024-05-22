package com.example.cleaberservice.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val context: Context,
    private val orders: Map<String, Order>,
    private val navController: NavController?
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private var keys = orders.keys.toTypedArray()
    private var selected: Int = -1

    fun updateKeys() {
        keys = orders.keys.toTypedArray()
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[keys[position]]

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val orderDateLong = order?.date
        if (orderDateLong is Long) {
            val orderDate = Date(orderDateLong)
            holder.tvDate.text = formatter.format(orderDate)
        } else {
            Log.d("MyLog", "Invalid date: $orderDateLong")
        }
        holder.tvAddress.text = order?.address

        if(selected == holder.adapterPosition)
            holder.relativeLayout.visibility = View.VISIBLE
        else
            holder.relativeLayout.visibility = View.GONE

        holder.linearLayout.setOnClickListener {
            if(selected == holder.adapterPosition && holder.relativeLayout.visibility == View.VISIBLE)
                holder.relativeLayout.visibility = View.GONE
            else if(selected == holder.adapterPosition && holder.relativeLayout.visibility == View.GONE)
                holder.relativeLayout.visibility = View.VISIBLE
            else {
                selected = holder.adapterPosition
                notifyDataSetChanged()
            }
        }

        holder.bAbout.setOnClickListener {
            if(navController != null) {
                val bundle = bundleOf("orderId" to order?.id)
                when(DB.users[DB.auth.currentUser!!.uid]!!.role) {
                    1 -> {
                        navController.navigate(R.id.orderDetailsFragment, bundle)
                    }
                    else -> {
                        Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.listOrderTVDate)
        val tvAddress: TextView = view.findViewById(R.id.listOrderTVDAddress)
        val bAbout: Button = view.findViewById(R.id.listOrderBAbout)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.listOrderRelativeLayout)
        val linearLayout: LinearLayout = view.findViewById(R.id.listOrderLinearLayout)
    }
}
