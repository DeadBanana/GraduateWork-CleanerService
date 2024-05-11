package com.example.cleaberservice.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cleaberservice.R
import com.google.android.material.navigation.NavigationView

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
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.userMainFragment,
            R.id.userTestFragment
        ), drawerLayout)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.UserActivityToolbar)
        toolbar.title = navController.currentDestination?.label
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}