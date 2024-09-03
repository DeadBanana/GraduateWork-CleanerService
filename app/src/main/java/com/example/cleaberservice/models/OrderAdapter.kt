package com.example.cleaberservice.models

import android.content.Context
import android.os.Bundle
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
    private val navController: NavController?,
    private val visibility: Boolean
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    constructor(context: Context, orders: Map<String, Order>, navController: NavController?) :
            this(context, orders, navController, false)

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
            if(selected == holder.adapterPosition && holder.relativeLayout.visibility == View.VISIBLE) {
                holder.relativeLayout.visibility = View.GONE
                selected = -1
            }
            else {
                selected = holder.adapterPosition
                notifyDataSetChanged()
            }
        }

        holder.bAbout.setOnClickListener {
            if(navController != null) {
                val role = DB.users[DB.auth.currentUser!!.uid]!!.role
                val bundle = Bundle()
                bundle.putString("orderId", order?.id)
                bundle.putInt("role", role)
                bundle.putBoolean("visibility", visibility)
                if(role == 1)
                    navController.navigate(R.id.orderDetailsFragment, bundle)
                else
                    navController.navigate(R.id.orderDetailsFragment2, bundle)
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
