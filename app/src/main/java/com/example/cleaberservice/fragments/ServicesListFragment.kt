package com.example.cleaberservice.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.ServiceAdapter
import com.example.cleaberservice.models.SharedViewModel

class ServicesListFragment : Fragment() {
    val viewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_services_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)
        val bDone = view.findViewById<Button>(R.id.ServicesListFragmentBDone)
        val lvServices = view.findViewById<RecyclerView>(R.id.ServicesListFragmentLVServices)
        val adapter = ServiceAdapter(view.context, DB.services)
        viewModel.selectedItems.observe(viewLifecycleOwner) { items ->
            adapter.selectedServices = items
        }
        lvServices.setHasFixedSize(true)
        lvServices.layoutManager = LinearLayoutManager(view.context)
        lvServices.adapter = adapter
        bDone.setOnClickListener {
            viewModel.selectedItems.value = adapter.selectedServices
            navController.popBackStack()
        }
    }
}