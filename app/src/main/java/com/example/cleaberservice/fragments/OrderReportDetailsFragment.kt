package com.example.cleaberservice.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.ImageDialog
import com.example.cleaberservice.models.Order
import com.example.cleaberservice.models.User
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class OrderReportDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_report_details, container, false)
    }

    private lateinit var images: LinearLayout
    private val bitmaps = mutableListOf<Bitmap>()
    private val targets = mutableListOf<Target>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val tvComment: TextView = view.findViewById(R.id.OrderReportDetailFragmentTVComment)
        images = view.findViewById(R.id.OrderReportDetailFragmentLLImageContainer)

        lateinit var contextOrder: Order
        val orderId = arguments?.getString("orderId")
        if (orderId != null)
            contextOrder = DB.orders[orderId]!!

        tvComment.text = contextOrder.comment

        contextOrder.photos["${User.ROLE}1"]?.values?.forEach { uri ->
            val target = object : Target {
                override fun onBitmapLoaded(p0: Bitmap?, p1: Picasso.LoadedFrom?) {
                    p0.let {
                        bitmaps.add(it!!)
                    }
                    refreshPhotos()
                }

                override fun onBitmapFailed(p0: Exception?, p1: Drawable?) {
                    Log.d("My Log", "Failed to load image<OrderDetailFragment>", p0)
                }

                override fun onPrepareLoad(p0: Drawable?) {
                }
            }
            targets.add(target)
            Picasso.get().load(uri).into(target)
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