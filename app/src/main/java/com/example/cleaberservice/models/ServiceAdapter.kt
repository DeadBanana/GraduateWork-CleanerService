package com.example.cleaberservice.models

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.cleaberservice.R

class ServiceAdapter(context: Context, private val services: Map<String, Service>) : BaseAdapter() {
    private var keys = services.keys.toTypedArray()
    var selectedServices = arrayListOf<String>()

    fun updateKeys() {
        keys = services.keys.toTypedArray()
    }

    override fun getCount(): Int {
        return services.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Service? {
        return services[keys[position]]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?:
        LayoutInflater.from(parent.context).inflate(R.layout.list_services, parent, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.listServicesLinearLayout)
        val relativeLayout = view.findViewById<RelativeLayout>(R.id.listServicesRelativeLayout)
        val tvName = view.findViewById<TextView>(R.id.listServicesTVName)
        val tvDescription = view.findViewById<TextView>(R.id.listServicesTVDescription)
        val bAdd = view.findViewById<Button>(R.id.listServicesBAdd)
        val bRemove = view.findViewById<Button>(R.id.listServicesBRemove)
        val checkMark = view.findViewById<ImageView>(R.id.listServicesIVCheckMark)

        tvName.text = getItem(position)?.name
        tvDescription.text = getItem(position)?.description

        if(selectedServices.contains(keys[position])) {
            checkMark.visibility = View.VISIBLE
            relativeLayout.visibility = View.GONE
            bAdd.visibility = View.GONE
            bRemove.visibility = View.VISIBLE
        }
        else {
            checkMark.visibility = View.GONE
            relativeLayout.visibility = View.GONE
            bAdd.visibility = View.VISIBLE
            bRemove.visibility = View.GONE
        }

        linearLayout.setOnClickListener {
            if(relativeLayout.visibility == View.GONE) {
                relativeLayout.visibility = View.VISIBLE
            }
            else if(relativeLayout.visibility == View.VISIBLE) {
                relativeLayout.visibility = View.GONE
            }
        }

        bAdd.setOnClickListener {
            selectedServices.add(keys[position])
            checkMark.visibility = View.VISIBLE
            relativeLayout.visibility = View.GONE
            bAdd.visibility = View.GONE
            bRemove.visibility = View.VISIBLE
        }

        bRemove.setOnClickListener {
            selectedServices.remove(keys[position])
            checkMark.visibility = View.GONE
            relativeLayout.visibility = View.GONE
            bAdd.visibility = View.VISIBLE
            bRemove.visibility = View.GONE
        }
        return  view
    }
}