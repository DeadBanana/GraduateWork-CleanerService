package com.example.cleaberservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.cleaberservice.models.DB
import com.google.android.gms.tasks.OnCompleteListener

class MainActivity : AppCompatActivity() {
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edEmail = findViewById(R.id.LoginActivityEDEmail)
        edPassword = findViewById(R.id.LoginActivityEDPassword)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = DB.auth.currentUser
        if (currentUser != null) {
            Log.d("MyLog", "Current User is ${DB.users[currentUser.uid]?.name}<LoginActivity>")
        }
        else
            Log.d("MyLog", "Current User is null<LoginActivity>")
        Toast.makeText(this, "OnStart", Toast.LENGTH_SHORT).show()
    }

    fun BLoginClick(view: View) {
        val email = edEmail.text.trim().toString()
        val password = edPassword.text.trim().toString()
        if (email.isNotEmpty() && password.isNotEmpty())
            DB.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener
            {task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "User Auth Successful", Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Successful<LoginActivity>")
                }
                else
                {
                    Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Failed<LoginActivity>")
                    edPassword.text.clear()
                }

            })
        else
        {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            return
        }
    }
}