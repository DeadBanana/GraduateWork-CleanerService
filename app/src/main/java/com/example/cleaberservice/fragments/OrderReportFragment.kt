package com.example.cleaberservice.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.ImageDialog
import com.example.cleaberservice.models.ImageUtils
import com.example.cleaberservice.models.Order

class OrderReportFragment : Fragment() {

    private lateinit var imageContainer: LinearLayout
    private lateinit var addPhoto: LinearLayout
    private val bitmaps = mutableListOf<Bitmap>()
    private var photoUri: Uri? = null
    private val CAMERA_CAPTURE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_report, container, false)
    }

    override fun onResume() {
        super.onResume()
        refreshPhotos()
    }

    private fun refreshPhotos() {
        imageContainer.removeAllViews()
        imageContainer.addView(addPhoto)
        bitmaps.forEach { bitmap ->
            addImageToContainer(bitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        ImageUtils.handleCameraPermissionResult(requestCode, grantResults) {
            photoUri = ImageUtils.takeImage(this, CAMERA_CAPTURE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImageUtils.handleImageCaptureResult(requestCode, resultCode, data, CAMERA_CAPTURE_REQUEST) { uri ->
            uri?.let {
                val bitmap = BitmapFactory.decodeFile(ImageUtils.getCurrentPhotoPath())
                bitmaps.add(bitmap)
            }
        }
    }

    fun takeImage() {
        if (ImageUtils.isCameraPermissionGranted(requireContext())) {
            photoUri = ImageUtils.takeImage(this, CAMERA_CAPTURE_REQUEST)
        } else {
            ImageUtils.requestCameraPermission(this)
        }
    }

    private fun addImageToContainer(bitmap: Bitmap) {
        val imageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(140.dpToPx(), 140.dpToPx()).apply {
                marginEnd = 8.dpToPx()
            }
            setImageBitmap(bitmap)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = resources.getDrawable(R.drawable.image_container_background, null)

            setOnClickListener {
                val dialog = ImageDialog(bitmaps, bitmaps.indexOf(bitmap))
                dialog.regDelegate { refreshPhotos() }
                dialog.show(childFragmentManager, "ImageDialog")
            }
        }
        imageContainer.addView(imageView, imageContainer.childCount - 1)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val edComment: EditText = view.findViewById(R.id.OrderReportFragmentEDComment)
        val bDone: Button = view.findViewById(R.id.OrderReportFragmentBSubmit)
        imageContainer = view.findViewById(R.id.OrderReportFragmentLLImageContainer)
        addPhoto = view.findViewById(R.id.OrderReportFragmentLLAddPhoto)

        addPhoto.setOnClickListener {
            takeImage()
        }

        lateinit var contextOrder: Order
        val orderId = arguments?.getString("orderId")
        if(orderId != null)
            contextOrder = DB.orders[orderId]!!

        bDone.setOnClickListener {
            val comment = edComment.text.trim().toString()
            if(bitmaps.isEmpty()) {
                Toast.makeText(requireContext(),getText(R.string.ex_no_photo_added),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DB.addPhotosToOrder(contextOrder, bitmaps)
            if(comment.isNotEmpty())
                contextOrder.comment = comment
            DB.addReport(contextOrder)
            Toast.makeText(requireContext(),getText(R.string.msg_success),Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }
}