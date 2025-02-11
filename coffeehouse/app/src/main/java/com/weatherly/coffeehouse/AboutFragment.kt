/*
Class file for AboutFragment
Author:  Chip Weatherly
Date:    2/11/2025
Purpose: Holds logic for the About page, containing app info and Privacy Policy
Changelog:
2/11/25 Initial creation - CW
 */

package com.weatherly.coffeehouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

class AboutFragment : Fragment() {

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
        val aboutView = inflater.inflate(R.layout.fragment_about, container, false)

        // assign buttons to variables
        val backButton = aboutView.findViewById<Button>(R.id.back_button)
        // bind back button to listener
        backButton.setOnClickListener {
            // called when back button is pressed
            closeFragment()
        }

        return aboutView
    }

    // called when back button is pressed
    private fun closeFragment() {
        requireActivity().supportFragmentManager.let {
            val transaction: FragmentTransaction = it.beginTransaction()
            transaction.remove(this)
            transaction.commit()
        }
    }

}