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
import android.widget.Toast
import com.example.cleaberservice.activity.CleanerActivity
import com.example.cleaberservice.activity.RegistrationActivity
import com.example.cleaberservice.activity.UserActivity
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

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
        pbProgress.visibility = View.VISIBLE
        val currentUser = DB.auth.currentUser
        if (currentUser != null) {
            val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("uId", currentUser.uid)
                apply()
            }
            val ref = DB.database.getReference(User.ROOT).child(currentUser.uid)
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        pbProgress.visibility = View.GONE
                        NavigateByRole(user.role)
                        Log.d("MyLog", "Current User is ${user.name}<LoginActivity>")
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("MyLog", "loadUser:onCancelled", databaseError.toException())
                }
            })
            pbProgress.visibility = View.GONE
        }
        else {
            Log.d("MyLog", "Current User is null<LoginActivity>")
            pbProgress.visibility = View.GONE
        }
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
                    NavigateByRole(DB.users[task.result.user!!.uid]!!.role)
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
            1 -> {
                val intent = Intent(this, CleanerActivity::class.java)
                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
            }
        }
    }
}