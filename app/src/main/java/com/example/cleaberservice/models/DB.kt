package com.example.cleaberservice.models

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object DB {
    val database = FirebaseDatabase.getInstance("https://cleanerservice-be312-default-rtdb.europe-west1.firebasedatabase.app/")
    var users: MutableMap<String, User> = mutableMapOf()
    var auth: FirebaseAuth
    private var adapterDelegate = mutableListOf<() -> Unit>()

    init {
        addValueEventListener(database.getReference(User.ROOT), users)
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
                Log.d("MyLog", "Data read Error<DB>", databaseError.toException())
            }
        })
    }

    fun <T> updateFirebase(ref: DatabaseReference, map: MutableMap<String, T>) {
        // Обновление данных в Firebase
        ref.updateChildren(map as Map<String, Any>)
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