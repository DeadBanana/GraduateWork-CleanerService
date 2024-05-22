package com.example.cleaberservice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB

class UserTestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvName = view.findViewById<TextView>(R.id.UserTestFragmentTVName)
//        val args: UserTestFragmentArgs by navArgs()
//        val uId = args.uId
//        if(uId != null)
//            tvName.text = "Logged user: ${DB.users[uId]?.name}"
        val uId = arguments?.getString("uId")
        if(uId != null)
            tvName.text = "Logged user: ${DB.users[arguments?.getString("uId")]?.name}"

    }
}