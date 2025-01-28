/*
Class file for
Author:  Chip Weatherly
Date:    1/15/2025
Purpose: Holds logic for "settings" menu fragment
Changelog:
1/15/25 Initial creation - CW
1/21/25 Added functionality for menu buttons - CW
1/27/25 Added setTimers functionality allowing users to manually set timer length and interval - CW
 */
package com.weatherly.coffeehouse

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction


class SetTimeFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment, assign to variable
        val setTimeView = inflater.inflate(R.layout.fragment_set_time, container, false)

        // assign number pickers to variables
        val minutePicker = setTimeView.findViewById<NumberPicker>(R.id.minute_entry)
        val intervalPicker = setTimeView.findViewById<NumberPicker>(R.id.interval_entry)

        // set minutePicker params (minutes)
        minutePicker.setMinValue(1)
        minutePicker.setMaxValue(120)
        // set intervalPicker params (seconds)
        intervalPicker.setMinValue(0)
        intervalPicker.setMaxValue(10)

        // assign buttons to variables
        val backButton = setTimeView.findViewById<Button>(R.id.back_button)
        val confirmButton = setTimeView.findViewById<Button>(R.id.confirm_button)

        // bind back button to listener
        backButton.setOnClickListener {
            // called when back button is pressed
            closeFragment()
        }
        // bind confirm button to listener
        confirmButton.setOnClickListener {
            // called when confirm button is pressed
            setTimers(setTimeView)
        }
        return setTimeView
    }

    // called when back button is pressed
    private fun closeFragment() {
        requireActivity().supportFragmentManager.let {
            val transaction: FragmentTransaction = it.beginTransaction()
            transaction.remove(this)
            transaction.commit()
        }
    }

    // called when confirm button is pressed
    private fun setTimers(view: View) {
        // access sharedPrefs
        val sharedPrefs = requireActivity().getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}_sharedPreferences",
            Context.MODE_PRIVATE)
        // assign number pickers to variables
        val minutePicker = view.findViewById<NumberPicker>(R.id.minute_entry)
        val intervalPicker = view.findViewById<NumberPicker>(R.id.interval_entry)
        // assign chosen values to variables & convert to Long
        val clockTime = (minutePicker.value * 60000).toLong()
        println(clockTime)
        val intervalTime = (intervalPicker.value * 1000).toLong()
        // save minutePicker value to sharedPrefs
        sharedPrefs.edit().putLong("clock_time", clockTime).apply()
        println(sharedPrefs.getLong("clock_time", clockTime))
        // save intervalTime to sharedPrefs
        sharedPrefs.edit().putLong("interval_time", intervalTime).apply()
        // restart the timers with the new values
        (activity as MainActivity?)!!.changeTimers()
        closeFragment()
    }

    // placeholder, to be removed before release
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment setTimeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SetTimeFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}