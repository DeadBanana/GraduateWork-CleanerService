package com.example.cleaberservice

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.cleaberservice.activity.RegistrationActivity
import com.example.cleaberservice.activity.UserActivity
import com.example.cleaberservice.models.DB
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    lateinit var pbProgress: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        edEmail = findViewById(R.id.LoginActivityEDEmail)
        edPassword = findViewById(R.id.LoginActivityEDPassword)
        pbProgress = findViewById(R.id.LoginActivityRLoad)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = DB.auth.currentUser
        if (currentUser != null) {
            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("uId", currentUser.uid)
                apply()
            }
            Log.d("MyLog", "Current User is ${DB.users[currentUser.uid]?.name}<LoginActivity>")
            NavigateByRole(0)
        }
        else
            Log.d("MyLog", "Current User is null<LoginActivity>")
        Toast.makeText(this, "OnStart", Toast.LENGTH_SHORT).show()
    }

    fun BLoginClick(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        pbProgress.visibility = View.VISIBLE
        val email = edEmail.text.trim().toString()
        val password = edPassword.text.trim().toString()
        if (email.isNotEmpty() && password.isNotEmpty())
            DB.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener
            {task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "User Auth Successful", Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Successful<LoginActivity>")
                    pbProgress.visibility = View.GONE
                    val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("uId", task.result.user!!.uid)
                        apply()
                    }
                    NavigateByRole(0)
                }
                else
                {
                    Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Failed<LoginActivity>")
                    pbProgress.visibility = View.GONE
                    edPassword.text.clear()
                }

            })
        else
        {
            Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show()
            pbProgress.visibility = View.GONE
            return
        }
    }

    fun BRegistrationClick(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    fun NavigateByRole(role: Int) {
        when(role) {
            else -> {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
        }
    }
}