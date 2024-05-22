package com.example.cleaberservice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R

class UserMainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val button1 = view.findViewById<Button>(R.id.UserMainFragmentBNavHistory)
        val button2 = view.findViewById<Button>(R.id.UserMainFragmentBNavOrdering)

        button1.setOnClickListener {
            navController.navigate(R.id.historyListFragment)
        }

        button2.setOnClickListener {
            navController.navigate(R.id.orderSubmittingFragment)
        }
    }
}