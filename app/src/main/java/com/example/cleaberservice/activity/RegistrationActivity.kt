package com.example.cleaberservice.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.User
import com.google.android.gms.tasks.OnCompleteListener

class RegistrationActivity : AppCompatActivity() {
    lateinit var edName: EditText
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    lateinit var edRePassword: EditText
    lateinit var pbProgress: RelativeLayout
    var role = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        edName = findViewById(R.id.RegistrationActivityEDName)
        edEmail = findViewById(R.id.RegistrationActivityEDEmail)
        edPassword = findViewById(R.id.RegistrationActivityEDPassword)
        edRePassword = findViewById(R.id.RegistrationActivityEDRePassword)
        pbProgress = findViewById(R.id.RegistrationActivityRLoad)
        edName.setOnLongClickListener {
            when (role) {
                1 -> {
                    role = 0
                    Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show()
                    true
                }
                0 -> {
                    role = 1
                    Toast.makeText(this, "Changed", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }

    fun BRegistrationClick(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        val name = edName.text.trim().toString()
        val email = edEmail.text.trim().toString()
        val password = edPassword.text.trim().toString()
        val rePassword = edRePassword.text.trim().toString()
        pbProgress.visibility = View.VISIBLE
        var isError = false
        if(name.isEmpty()) {
            edName.error = getString(R.string.ex_required)
            isError = true
        }
        if(email.isEmpty()) {
            edEmail.error = getString(R.string.ex_required)
            isError = true
        }
        if(password.isEmpty() || password.length <= 6) {
            edPassword.error = getString(R.string.ex_password)
            isError = true
        }
        if(rePassword != password) {
            edRePassword.error = getString(R.string.ex_rep_password)
            isError = true
        }
        if(isError) {
            pbProgress.visibility = View.GONE
            Toast.makeText(this, R.string.msg_wrong_data, Toast.LENGTH_SHORT).show()
            return
        }


        DB.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
            OnCompleteListener { task ->
                if(task.isSuccessful) {
                    val uId = DB.auth.currentUser?.uid
                    val user = User(uId!!, name, email, role)
                    DB.users[uId] = user
                    DB.updateFirebase(DB.database.getReference(User.PATH.ROOT), DB.users)
                    pbProgress.visibility = View.GONE
                    val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("uId", task.result.user!!.uid)
                        apply()
                    }
                    Toast.makeText(this, R.string.msg_success, Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Successful<AuthActivity>)")
                    NavigateByRole(role)
                }
                else {
                    pbProgress.visibility = View.GONE
                    Toast.makeText(this, R.string.msg_fail, Toast.LENGTH_SHORT).show()
                    Log.d("MyLog", "User(${email}, ${password}) Auth Failed<AuthActivity>")
                }
            })
    }

    fun BLoginClick(view: View) {
        finish()
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