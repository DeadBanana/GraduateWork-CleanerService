package com.example.cleaberservice.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.cleaberservice.R
import com.example.cleaberservice.models.DB
import com.example.cleaberservice.models.Order
import com.example.cleaberservice.models.SharedViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class OrderSubmittingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_submiting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        val edAddress = view.findViewById<TextView>(R.id.OrderSubmittingFragmentEDAddress)
        val edDate = view.findViewById<TextView>(R.id.OrderSubmittingFragmentEDDate)
        val edDescription = view.findViewById<TextView>(R.id.OrderSubmittingFragmentEDDescription)
        val bServices = view.findViewById<Button>(R.id.OrderSubmittingFragmentBServices)
        val bDone = view.findViewById<Button>(R.id.OrderSubmittingFragmentBSubmit)

        var listServices = arrayListOf<String>()
        var dateLong: Long = Calendar.getInstance().time.time

        val viewModel: SharedViewModel by activityViewModels()

        viewModel.selectedItems.observe(viewLifecycleOwner) {items ->
            listServices = items
        }

        edDate.setOnClickListener {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH)
            val day = now.get(Calendar.DAY_OF_MONTH)

            val dateSetListener = DatePickerDialog.OnDateSetListener { _, selectedYear, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, selectedYear)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val date = selectedDate.time
                val format = android.icu.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val formattedDate = format.format(date)
                edDate.text = formattedDate
                dateLong = date.time
                Log.d("MyLog", "Selected date: $date")
            }
            DatePickerDialog(view.context, dateSetListener, year, month, day).show()
        }

        bServices.setOnClickListener {
            val bundle = Bundle().apply {
                putStringArrayList("services", listServices)
            }
            val fragment = ServicesListFragment().apply {
                arguments = bundle
            }
            navController.navigate(R.id.action_orderSubmittingFragment_to_servicesListFragment, bundle)
        }

        bDone.setOnClickListener {
            val address = edAddress.text.trim().toString()
            val date = edDate.text.trim().toString()
            var description = edDescription.text.trim().toString()

            var isError = false

            if(address.isEmpty()) {
                edAddress.error = getString(R.string.ex_required)
                isError = true
            }
            if(date.isEmpty()) {
                edDate.error = getString(R.string.ex_required)
                isError = true
            }
//            if(!date.matches(Regex("^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)" +
//                        "(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)" +
//                        "0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])" +
//                        "|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])" +
//                        "(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$"))) {
//                edDate.error = getString(R.string.ex_date_format)
//                isError = true
//            }
            if(listServices.isEmpty()) {
                bServices.error = getString(R.string.ex_required)
                isError = true
            }

            if(isError)
                return@setOnClickListener

//            val dateLong: Long
//            try {
//                val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
//                dateLong = format.parse(date)?.time ?: 0
//            }
//            catch (ex: ParseException) {
//                edDate.error = getString(R.string.ex_date_format)
//                return@setOnClickListener
//            }
            if(description.isEmpty())
                description = "Без описания"
            val order = Order(address, dateLong, description, false)
            listServices.forEach {
                order.services[it] = true
            }
            DB.addOrder(order)
            viewModel.selectedItems.value?.clear()
            Toast.makeText(view.context, R.string.msg_success, Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }
}