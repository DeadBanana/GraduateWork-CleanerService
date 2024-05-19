package com.example.cleaberservice.models

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(context: Context, private val orders: Map<String, Order>,
                   private val navController: NavController?) : BaseAdapter() {
    private var keys =  orders.keys.toTypedArray()
    private var selected: Int = -1

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

        val tvDate: TextView = view.findViewById(R.id.listOrderTVDate)
        val tvAddress: TextView = view.findViewById(R.id.listOrderTVDAddress)
        val bAbout: Button = view.findViewById(R.id.listOrderBAbout)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.listOrderRelativeLayout)
        val linearLayout: LinearLayout = view.findViewById(R.id.listOrderLinearLayout)

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val orderDateLong = getItem(position)?.date
        if (orderDateLong is Long) {
            val orderDate = Date(orderDateLong)
            tvDate.text = formatter.format(orderDate)
        } else {
            Log.d("MyLog", "Invalid date: $orderDateLong")
        }
        tvAddress.text = getItem(position)?.address

        if(selected == position)
            relativeLayout.visibility = View.VISIBLE
        else
            relativeLayout.visibility = View.GONE

        linearLayout.setOnClickListener {
            if(selected == position && relativeLayout.visibility == View.VISIBLE)
                relativeLayout.visibility = View.GONE
            else if(selected == position && relativeLayout.visibility == View.GONE)
                relativeLayout.visibility = View.VISIBLE
            else {
                selected = position
                this.notifyDataSetChanged()
            }
        }

        bAbout.setOnClickListener {
            if(navController != null) {
                val bundle = bundleOf("orderId" to getItem(position)?.id)
                when(DB.users[DB.auth.currentUser!!.uid]!!.role) {
                    1 -> {
                        navController.navigate(R.id.orderDetailsFragment, bundle)
                    }
                    else -> {
                        Toast.makeText(view.context, "TODO", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return view
    }
}