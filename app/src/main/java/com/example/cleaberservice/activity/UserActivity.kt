package com.example.cleaberservice.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout.Directions
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cleaberservice.R
import com.example.cleaberservice.fragments.HistoryListFragmentDirections
import com.example.cleaberservice.fragments.UserMainFragmentDirections
import com.example.cleaberservice.models.DB
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class UserActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.UserActivityNavigationView)
        val host: NavHostFragment = supportFragmentManager.findFragmentById(R.id.UserActivityNavHosFragment)
            as NavHostFragment? ?: return
        navController = host.navController
        navView.setupWithNavController(navController)
//        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.userMainFragment,
            R.id.userTestFragment,
            R.id.historyListFragment,
            R.id.orderSubmittingFragment
        ), drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.UserActivityToolbar)
        toolbar.title = navController.currentDestination?.label
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
//        if(!DB.auth.currentUser!!.isEmailVerified) {
//            navView.menu.findItem(R.id.userMainFragment).isVisible = false
//        }
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.userMainFragment -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.userTestFragment -> {
                    val bundle = bundleOf("uId" to DB.auth.currentUser!!.uid)
                    navController.navigate(menuItem.itemId, bundle)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.historyListFragment -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                R.id.orderSubmittingFragment -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> false
            }
        }
        val bBack = findViewById<Button>(R.id.UserActivityBBack)
        bBack.setOnClickListener {
            DB.auth.signOut()
            finish()
        }
        val headerView = navView.getHeaderView(0)
        val userName = headerView.findViewById<TextView>(R.id.HeaderTVName)
        val userEmail = headerView.findViewById<TextView>(R.id.HeaderTVEmail)
        val sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val uId = sharedPreferences.getString("uId", "")
        uId.let {
            DB.addAdapter {
                userName.text = DB.users[it]?.name
                userEmail.text = DB.users[it]?.email
            }
        }
        DB.invokeDelegate()
    }
}