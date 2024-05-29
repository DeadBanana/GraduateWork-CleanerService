package com.example.cleaberservice.models

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.cleaberservice.R

class ImageDialog(private val images: MutableList<Bitmap>, private val initialPosition: Int, private val editable: Boolean):
DialogFragment() {

    constructor(images: MutableList<Bitmap>, initialPosition: Int): this(images, initialPosition, true)

    private var currentPosition = initialPosition
    private lateinit var delegate: () -> Unit

    fun regDelegate(function: () -> Unit) {
        delegate = function
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_image, container, false)
        val viewPager = view.findViewById<ViewPager>(R.id.dialog_view_pager)
        val deleteButton = view.findViewById<Button>(R.id.delete_button)

        viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun getCount(): Int {
                return images.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(context)
                imageView.setImageBitmap(images[position])
                container.addView(imageView)
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }
        }

        viewPager.currentItem = currentPosition

        if(!editable)
            deleteButton.visibility = View.GONE

        deleteButton.setOnClickListener {
            val position = viewPager.currentItem
            images.removeAt(position)
            viewPager.adapter?.notifyDataSetChanged()
            delegate.invoke()
            Toast.makeText(requireContext(), getText(R.string.msg_success), Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }
}