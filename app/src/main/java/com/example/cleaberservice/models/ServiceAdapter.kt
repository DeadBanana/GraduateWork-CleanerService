package com.example.cleaberservice.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R

class ServiceAdapter(
    private val context: Context,
    private val services: Map<String, Service>
) : RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {

    private var keys = services.keys.toTypedArray()
    var selectedServices = arrayListOf<String>()

    fun updateKeys() {
        keys = services.keys.toTypedArray()
    }

    override fun getItemCount(): Int {
        return services.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_services, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = services[keys[position]]

        holder.tvName.text = service?.name
        holder.tvDescription.text = service?.description

        if (selectedServices.contains(keys[position])) {
            holder.checkMark.visibility = View.VISIBLE
            holder.relativeLayout.visibility = View.GONE
            holder.bAdd.visibility = View.GONE
            holder.bRemove.visibility = View.VISIBLE
        } else {
            holder.checkMark.visibility = View.GONE
            holder.relativeLayout.visibility = View.GONE
            holder.bAdd.visibility = View.VISIBLE
            holder.bRemove.visibility = View.GONE
        }

        holder.linearLayout.setOnClickListener {
            if (holder.relativeLayout.visibility == View.GONE) {
                holder.relativeLayout.visibility = View.VISIBLE
            } else if (holder.relativeLayout.visibility == View.VISIBLE) {
                holder.relativeLayout.visibility = View.GONE
            }
        }

        holder.bAdd.setOnClickListener {
            selectedServices.add(keys[position])
            holder.checkMark.visibility = View.VISIBLE
            holder.relativeLayout.visibility = View.GONE
            holder.bAdd.visibility = View.GONE
            holder.bRemove.visibility = View.VISIBLE
        }

        holder.bRemove.setOnClickListener {
            selectedServices.remove(keys[position])
            holder.checkMark.visibility = View.GONE
            holder.relativeLayout.visibility = View.GONE
            holder.bAdd.visibility = View.VISIBLE
            holder.bRemove.visibility = View.GONE
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.listServicesLinearLayout)
        val relativeLayout = view.findViewById<RelativeLayout>(R.id.listServicesRelativeLayout)
        val tvName = view.findViewById<TextView>(R.id.listServicesTVName)
        val tvDescription = view.findViewById<TextView>(R.id.listServicesTVDescription)
        val bAdd = view.findViewById<Button>(R.id.listServicesBAdd)
        val bRemove = view.findViewById<Button>(R.id.listServicesBRemove)
        val checkMark = view.findViewById<ImageView>(R.id.listServicesIVCheckMark)
    }
}