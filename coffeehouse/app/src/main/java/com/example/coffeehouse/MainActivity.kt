/*
Main Activity file for Chess Clock Android App
Author:  Chip Weatherly
Date:    1/6/2025
Purpose: Holds logic for chess clock app main screen
Changelog:
1/6/25 Initial creation - CW
1/7/25 Added clock functionality - CW
1/8/25 Added ability to switch between clocks by clicking the screen - CW
1/10/25 Added functionality for drop-down menu
1/13/25 Added +5 seconds on switch as per Fischer timing
1/14/25 Altered display to show 5 second addition correctly
 */
package com.example.coffeehouse

import android.app.Dialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.MenuItem
import android.view.MotionEvent
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    // variables for navigation bar function
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    // hold user's selection of which player's turn it is
    private var timerSelected = 1
    // holds remaining time on each clock
    var clock1Time: Long = 600000
    var clock2Time: Long = 600000
    // init textView variables for the 2 clocks
    lateinit var clock1 : TextView
    lateinit var clock2 : TextView

    // runs on creation of the main activity, at app start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // assigning ID of the toolbar to a variable
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // uses the toolbar as an actionbar
        setSupportActionBar(toolbar)

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        // to make the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // assign NavigationView to a variable
        val mainNavigationView = findViewById<View>(R.id.navigation) as NavigationView
        // handles navigationview menu item selection
        mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            onNavigationItemSelected(menuItem)
        }

        // assign the clock view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)
        // assign initial values to clock text. To be changed into variables
        clock1.text = getString(R.string.initialClock)
        clock2.text = getString(R.string.initialClock)

        // Display start message to user
        startDialog()

        // begin the countdown!!!
        startTimers(timer1, timer2, timerSelected)
    }

    // function for starting message
    private fun startDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val body: TextView = dialog.findViewById(R.id.intro_card)
        body.text = getString(R.string.intro_message)

        // do something on touch
        dialog.show()

        dialog.window?.decorView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dialog.dismiss()
                true // Indicate that the touch event was handled
            } else {
                false
            }
        }


    }

    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Log.d("MainActivity", "Menu item selected: ${menuItem.title}")
        val id = menuItem.itemId
        when (id) {
            R.id.nav_dark_mode -> {
                // Handle the theme switch action
                Toast.makeText(this, "Theme switch selected", Toast.LENGTH_SHORT).show()
                toggleDarkMode()
                // val intent = Intent(this, HomeActivity::class.java)
                // startActivity(intent)
            }
            R.id.nav_settings -> {
                // Handle the settings action
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_reset -> {
                // Handle the reset action
                Toast.makeText(this, "Clocks Reset", Toast.LENGTH_SHORT).show()
                timer1.cancel()
                timer2.cancel()
                clock1Time = 600000
                clock2Time = 600000
                startTimers(timer1, timer2, timerSelected)
            }
        }
        // Close the navigation drawer after item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    // opens and closes the drawer when icon is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun toggleDarkMode() {
        // Get the current night mode
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            // If currently in dark mode, switch to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Toast.makeText(this, "Switched to Light Mode", Toast.LENGTH_SHORT).show()
        } else {
            // If currently in light mode, switch to dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Toast.makeText(this, "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
        }
    }

    // Timer object for player 1, 600000 = 10 minutes 1 = 1 millisecond
    private val timer1 = object : CountDownTimer(clock1Time, 1000) {
        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {
            clock1Time -= 1000
            setClockText(clock1, clock1Time)
        }

        // displays message when the time runs out
        override fun onFinish() {
            clock1.text = getString(R.string.timeOut)
        }
    }

    // Timer object for player 2
    private val timer2 = object : CountDownTimer(clock2Time, 1000) {
        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {
            clock2Time -= 1000
            setClockText(clock2, clock2Time)
        }
        // displays message when the time runs out
        override fun onFinish() {
            clock2.text = getString(R.string.timeOut)
        }
    }

    // method for capturing button click to swap clocks
    fun timerSwapButtonClick() {
        if (timerSelected == 1){
            // adds & displays extra 5 seconds when clock is stopped per Fischer rules
            clock1Time += 5000
            setClockText(clock1, clock1Time)
            timerSelected = 2
        } else {
            // adds & displays extra 5 seconds when clock is stopped per Fischer rules
            clock2Time += 5000
            setClockText(clock2, clock2Time)
            timerSelected = 1
        }
        startTimers(timer1, timer2, timerSelected)
    }

    // method for updating/displaying timer, pass in the timer and the clockTime value
    fun setClockText(clock: TextView, clockTime: Long) {
        val dec = DecimalFormat("00")
        val hour = (clockTime / 3600000) % 24
        val min = (clockTime / 60000) % 60
        val sec = (clockTime / 1000) % 60
        val formHour = dec.format(hour)
        val formMin = dec.format(min)
        val formSec = dec.format(sec)
        clock.text = getString(R.string.formatClock, formHour, formMin, formSec)
    }
}



// method initializes one timer at a time at program start
fun startTimers(timer1:CountDownTimer, timer2:CountDownTimer, timerSelected: Int) {
    // 'timerSelected' will hold the user's choice of who will go first
    if (timerSelected == 1){
        timer2.cancel()
        timer1.start()
    } else {
        timer1.cancel()
        timer2.start()
    }

}