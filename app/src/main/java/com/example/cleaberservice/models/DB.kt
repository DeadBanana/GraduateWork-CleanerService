package com.example.cleaberservice.models

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import kotlin.math.log

object DB {
    val database = FirebaseDatabase.getInstance("https://cleanerservice-be312-default-rtdb.europe-west1.firebasedatabase.app/")
    val storage = Firebase.storage
    val storageRef = storage.reference
    var users: MutableMap<String, User> = mutableMapOf()
    var services: MutableMap<String, Service> = mutableMapOf()
    var orders: MutableMap<String, Order> = mutableMapOf()
    var auth: FirebaseAuth
    private var adapterDelegate = mutableListOf<() -> Unit>()

    init {
        addValueEventListener(database.getReference(User.ROOT), users)
        addValueEventListener(database.getReference(Service.ROOT), services)
        addValueEventListener(database.getReference(Order.ROOT), orders)
        auth = Firebase.auth
    }

    private inline fun <reified T> addValueEventListener(ref: DatabaseReference, map: MutableMap<String, T>) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                map.clear()
                for (snapshot in dataSnapshot.children) {
                    val key = snapshot.key
                    val value = snapshot.getValue(T::class.java)
                    if (key != null && value != null) {
                        map[key] = value
                    }
                }
                invokeDelegate()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("MyLog", "Data read Error<DataBase>", databaseError.toException())
            }
        })
    }

    fun <T> updateFirebase(ref: DatabaseReference, map: MutableMap<String, T>) {
        ref.updateChildren(map as Map<String, Any>)
    }

    fun addOrder(order: Order) {
        val key = database.reference.child(Order.ROOT).push().key
        key?.let {
            val userId = auth.currentUser!!.uid
            val orderMap = mapOf(
                Order.ID to key,
                Order.USERID to userId,
                Order.ADDRESS to order.address,
                Order.DATE to order.date,
                Order.DESCRIPTION to order.description,
                Order.STATUS to order.status,
                Order.SERVICES to order.services,
                Order.VISIBILITY to order.visibility
            )
            val childUpdates = hashMapOf<String, Any>(
                "/${Order.ROOT}/$key" to orderMap,
                "/${User.ROOT}/$userId/${User.ORDERS}/$key" to true
            )
            database.reference.updateChildren(childUpdates)
            order.id = key
        }
    }

    fun addPhotosToOrder(order: Order, bitmaps: MutableList<Bitmap>) {
        val user = users[auth.currentUser!!.uid]
        user?.let {
            uploadImagesToFirebaseStorage(bitmaps, order.id, user.role) { photoUrls ->
                if (photoUrls != null) {
                    val urlsToMap = mutableMapOf<String, String>()
                    photoUrls.forEachIndexed { index, s ->
                        urlsToMap["${Order.PHOTOS}$index"] = s
                    }
                    order.photos[user.role.toString()] = urlsToMap
                    val orderRef = database.getReference("${Order.ROOT}/${order.id}/${Order.PHOTOS}/${User.ROLE}${user.role}")
                    orderRef.setValue(urlsToMap)
                } else {
                    Log.d("MyLog", "Error uploading images<DataBase>")
                }
            }
        } ?: run {
            Log.d("MyLog", "User not found<DataBase>")
        }
    }

    private fun uploadImagesToFirebaseStorage(bitmaps: MutableList<Bitmap>, orderId: String, role: Int, onComplete: (List<String>?) -> Unit) {
        val urls = mutableListOf<String>()
        var uploadCount = 0

        bitmaps.forEachIndexed { index, bitmap ->
            val imageRef = storageRef.child("order_images/${orderId}/${role}/${index}.jpg")

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos)
            val data = baos.toByteArray()

            val uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Log.d("MyLog","unsuccessful upload<DataBase>", it)
                onComplete(null)
                return@addOnFailureListener
            }.addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    urls.add(uri.toString())
                    uploadCount++
                    if (uploadCount == bitmaps.size) {
                        onComplete(urls)
                    }
                }.addOnFailureListener {
                    onComplete(null)
                    return@addOnFailureListener
                }
            }
        }
    }

    fun confirmOrder(order: Order) {
        val user = users[auth.currentUser?.uid]
        user?.let {
            if(user.role != 1) {
                Log.d("MyLog","WrongUserRole<DataBase>")
            }
            else {
                val childUpdates = hashMapOf<String, Any>(
                    "/${Order.ROOT}/${order.id}/${Order.VISIBILITY}" to false,
                    "/${User.ROOT}/${user.id}/${User.ORDERS}/${order.id}" to true,
                    "/${Order.ROOT}/${order.id}/${Order.CLEANERS}/${user.id}" to true
                )
                database.reference.updateChildren(childUpdates)
            }
        } ?: run {
            Log.d("MyLog", "Null User<DataBase>")
        }
    }

    fun addReport(order: Order) {
        val childUpdates = hashMapOf<String, Any>(
            "/${Order.ROOT}/${order.id}/${Order.STATUS}" to true,
            "/${Order.ROOT}/${order.id}/${Order.COMMENT}" to order.comment
        )
        database.reference.updateChildren(childUpdates)
    }

    fun addAdapter(adapter: () -> Unit) {
        adapterDelegate.add(adapter)
    }

    fun invokeDelegate() {
        for (adapter in adapterDelegate)
            adapter()
    }

    fun clearDelegate() {
        adapterDelegate.clear()
    }
}