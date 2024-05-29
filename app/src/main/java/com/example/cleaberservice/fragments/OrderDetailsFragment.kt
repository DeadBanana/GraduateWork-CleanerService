package com.example.cleaberservice.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.core.view.marginEnd
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.ImageDialog
import com.example.cleaberservice.models.Order
import com.example.cleaberservice.models.User
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_details, container, false)
    }

    private lateinit var images: LinearLayout
    private val bitmaps = mutableListOf<Bitmap>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val tvDate: TextView = view.findViewById(R.id.OrderDetailsFragmentTVDate)
        val tvAddress: TextView = view.findViewById(R.id.OrderDetailsFragmentTVAddress)
        val tvUserRole: TextView = view.findViewById(R.id.OrderDetailsFragmentTVUserRole)
        val tvClientName: TextView = view.findViewById(R.id.OrderDetailsFragmentTVClientName)
        val tvClientEmail: TextView = view.findViewById(R.id.OrderDetailsFragmentTVClientEmail)
        val tvDescription: TextView = view.findViewById(R.id.OrderDetailsFragmentTVDescription)

        val llContainer: LinearLayout = view.findViewById(R.id.OrderDetailsFragmentLLImageViewContainer)
        images = view.findViewById(R.id.OrderDetailsFragmentLLImageContainer)

        val lvServices: ListView = view.findViewById(R.id.OrderDetailsFragmentLVServices)
        val bRespond: Button = view.findViewById(R.id.OrderDetailsFragmentBRespond)
        val llRole: LinearLayout = view.findViewById(R.id.OrderDetailsFragmentLLUserRole)

        lateinit var contextOrder: Order
        val orderId = arguments?.getString("orderId")
        if(orderId != null)
            contextOrder = DB.orders[orderId]!!

        val uris = contextOrder.photos["${User.ROLE}0"]?.values

        if(uris == null)
            llContainer.visibility = View.GONE
        else {
            uris.forEach { uri ->
                Picasso.get().load(uri).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(p0: Bitmap?, p1: Picasso.LoadedFrom?) {
                        p0.let {
                            bitmaps.add(it!!)
                        }
                        refreshPhotos()
                        llContainer.visibility = View.VISIBLE
                    }

                    override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                        llContainer.visibility = View.GONE
                        Log.d("My Log", "Failed to load image<OrderDetailFragment>", p0)
                    }

                    override fun onPrepareLoad(p0: Drawable?) {
                    }
                })
            }
        }

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        tvDate.text = formatter.format(contextOrder.date)
        tvAddress.text = contextOrder.address
        tvClientName.text = DB.users[contextOrder.userId]!!.name
        tvClientEmail.text = DB.users[contextOrder.userId]!!.email
        tvDescription.text = contextOrder.description
        val servicesName = mutableListOf<String>()
        contextOrder.services.keys.forEach {
            servicesName.add(DB.services[it]!!.name)
        }
        val servicesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, servicesName)
        lvServices.adapter = servicesAdapter

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


        bRespond.setOnClickListener {
            DB.confirmOrder(contextOrder)
            navController.popBackStack()
            Toast.makeText(view.context, "Pressed", Toast.LENGTH_SHORT).show()
            Log.d("MyLog","Button Pressed<OrderDetailsFragment>")
        }
    }

    fun addImage(bitmap: Bitmap) {
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(140.dpToPx(), 140.dpToPx()).apply {
                marginEnd = 8.dpToPx()
            }
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = resources.getDrawable(R.drawable.image_container_background, null)

            setOnClickListener {
                val dialog = ImageDialog(bitmaps, bitmaps.indexOf(bitmap), false)
                dialog.regDelegate { refreshPhotos() }
                dialog.show(childFragmentManager, "ImageDialog")
            }
        }
        images.addView(imageView, images.childCount)
    }

    fun refreshPhotos() {
        images.removeAllViews()
        bitmaps.forEach { bitmap ->
            addImage(bitmap)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}