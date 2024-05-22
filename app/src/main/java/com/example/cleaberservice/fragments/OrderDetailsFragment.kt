package com.example.cleaberservice.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val tvDate: TextView = view.findViewById(R.id.OrderDetailsFragmentTVDate)
        val tvAddress: TextView = view.findViewById(R.id.OrderDetailsFragmentTVAddress)
        val tvUserRole: TextView = view.findViewById(R.id.OrderDetailsFragmentTVUserRole)
        val tvClientName: TextView = view.findViewById(R.id.OrderDetailsFragmentTVClientName)
        val tvClientEmail: TextView = view.findViewById(R.id.OrderDetailsFragmentTVClientEmail)
        val tvDescription: TextView = view.findViewById(R.id.OrderDetailsFragmentTVDescription)
        val bRespond: Button = view.findViewById(R.id.OrderDetailsFragmentBRespond)
        val llRole: LinearLayout = view.findViewById(R.id.OrderDetailsFragmentLLUserRole)

        lateinit var contextOrder: Order
        val orderId = arguments?.getString("orderId")
        if(orderId != null)
            contextOrder = DB.orders[orderId]!!
        val role = arguments?.getInt("role")
        if(role != null) {
            when(role) {
                0 -> {
                    tvUserRole.text = getString(R.string.cleaner)
                    if(DB.orders[orderId]!!.cleaners.isNotEmpty()) {
                        val cleanerId = DB.orders[orderId]!!.cleaners.entries.first { x -> x.value }.key
                        tvClientName.text = DB.users[cleanerId]!!.name
                        tvClientEmail.text = DB.users[cleanerId]!!.email
                    }
                    else
                        llRole.visibility = View.GONE
                }
            }
        }
        val visibility = arguments?.getBoolean("visibility")
        if(visibility != null) {
            if(!visibility)
                bRespond.visibility = View.GONE
        }

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        tvDate.text = formatter.format(contextOrder.date)
        tvAddress.text = contextOrder.address
        tvClientName.text = DB.users[contextOrder.userId]!!.name
        tvClientEmail.text = DB.users[contextOrder.userId]!!.email
        tvDescription.text = contextOrder.description

        bRespond.setOnClickListener {
            DB.confirmOrder(contextOrder)
            navController.popBackStack()
            Toast.makeText(view.context, "Pressed", Toast.LENGTH_SHORT).show()
            Log.d("MyLog","Button Pressed<OrderDetailsFragment>")
        }
    }
}