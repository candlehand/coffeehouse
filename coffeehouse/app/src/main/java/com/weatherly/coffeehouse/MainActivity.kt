/*
Main Activity file for Chess Clock Android App
Author:  Chip Weatherly
Date:    1/6/2025
Purpose: Holds logic for chess clock app main screen
Changelog:
1/6/25 Initial creation - CW
1/7/25 Added clock functionality - CW
1/8/25 Added ability to switch between clocks by clicking the screen - CW
1/10/25 Added functionality for drop-down menu - CW
1/13/25 Added +5 seconds on switch as per Fischer timing - CW
1/14/25 Altered display to show 5 second addition correctly, added intro message
        Timer only starts after an initial click - CW
1/16/25 Timers now persist when activity is restarted i.e. dark/light mode switch
        Dark mode slider now correctly updates when mode is switched - CW
1/27/25 Added ability to save variables in SharedPreferences
        Added settings menu, allowing users to choose minutes on timer
        and interval (in seconds)). Refactoring. - CW
1/28/25 Fixed broken onSavedState method, allowing app to preserve settings through dark/light
        change and screen orientation flip - CW
1/31/25 Dark mode toggle now functions correctly. Toggles on for dark mode, off for light. Pressing
        label or toggle both work correctly - CW
2/4/25  Reset button now resets to recent selection. Refactoring. - CW
2/11/25 Added about page containing Coffeehouse definition and privacy policy - CW
*/
package com.weatherly.coffeehouse

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
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
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    // allows access to SharedPreferences values
    private val sharedPrefs: SharedPreferences by lazy {
        getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}_sharedPreferences",
            Context.MODE_PRIVATE)
    }
    // user's selection of which player's turn it is
    private var timerSelected = 1
    // keep track of which clock is running
    var clock1Running = false
    var clock2Running = false
    // remaining time on each clock
    var clock1Time = 600000L
    var clock2Time = 600000L
    // interval when timers are switched
    private var interval = 5000L
    // init textView variables for the 2 clocks
    lateinit var clock1 : TextView
    lateinit var clock2 : TextView
    // track pause state
    private var isPaused = false
    // variables for navigation bar function
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    // init nav menu switch
    private lateinit var switchDarkMode: SwitchCompat
    // controls execution of onSwitched listener for switch
    private var isListenerEnabled = true

    // runs on creation of the main activity, at app start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //check user's dark/light mode preference
        val sharedPreferences = getSharedPreferences(
            "${BuildConfig.APPLICATION_ID}_sharedPreferences",
            MODE_PRIVATE
        )
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", true)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // assigning ID of the toolbar to a variable
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // assign the clock view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)

        // drawer layout instance
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        // makes the Navigation drawer icon always appear on the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // assign NavigationView to a variable
        val mainNavigationView = findViewById<View>(R.id.navigation) as NavigationView
        // handles navigationView menu item selection
        mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            onNavigationItemSelected(menuItem)
        }

        // Access the Switch from the NavigationView
        val navItem = mainNavigationView.menu.findItem(R.id.nav_dark_mode)
        val actionView = navItem.actionView
        if (actionView != null) {
            switchDarkMode = actionView.findViewById(R.id.switch_dark_mode)
        }

        switchDarkMode.isChecked = isDarkMode

        // Set a listener for the Switch, isListenerEnabled allows it to be ignored
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isListenerEnabled) {
                toggleDarkMode(isChecked)
            }
        }

        // if saved state exists, retrieve clock times & interval before proceeding
        if (savedInstanceState != null) {
            // Restores the state of the clocks from saved values
            clock1Time = savedInstanceState.getLong("clock1Time", 600000)
            clock2Time = savedInstanceState.getLong("clock2Time", 600000)
            interval = savedInstanceState.getLong("interval_time", 5000)
            timerSelected = savedInstanceState.getInt("timer_selected", 1)
            setClockText(clock1, clock1Time)
            setClockText(clock2, clock2Time)
        }

                // Displays start message to user, starts countdown when clicked
        startDialog(timer1, timer2, timerSelected)
    }

    // function for starting message, runs on initial start
    private fun startDialog(timer1:CountDownTimer, timer2:CountDownTimer, timerSelected: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val body: TextView = dialog.findViewById(R.id.intro_card)
        body.text = getString(R.string.intro_message)
        // set the clocks
        setClockText(clock1, clock1Time)
        setClockText(clock2, clock2Time)

        // closes the dialog when user clicks outside of it
        dialog.setCanceledOnTouchOutside(true)
        // do something on touch
        if (!isFinishing && !isDestroyed) {
            dialog.show()
        }

        dialog.window?.decorView?.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dialog.dismiss()
                view.performClick() // calling performClick as per accessibility suggestions
                // reset appropriate variables
                isPaused = false
                // begin the countdown!!!
                startTimers(timer1, timer2, timerSelected)
                true // Indicate that the touch event was handled
            } else {
                false
            }
        }
    }

    // to be called when values are changed from settings fragment, re-inits with new values
    fun changeTimers() {
        // check sharedPrefs for values
        println("before: $clock1Time")
        clock1Time = sharedPrefs.getLong("clock_time", 600000)
        clock2Time = sharedPrefs.getLong("clock_time", 600000)
        interval = sharedPrefs.getLong("interval_time", 5000)
        println("after: $clock1Time")
        // set clocks before message is displayed
        setClockText(clock1, clock1Time)
        setClockText(clock2, clock2Time)
        timerSelected = 1
        println("timerSelected: $timerSelected")
        startDialog(timer1, timer2, timerSelected)
    }

    // handles navigation menu selections
    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Log.d("MainActivity", "Menu item selected: ${menuItem.title}")
        when (menuItem.itemId) {
            R.id.nav_dark_mode -> {
                // Handle the theme switch action
                Toast.makeText(this, "Theme changed", Toast.LENGTH_SHORT).show()
                switchDarkMode.toggle()
            }
            R.id.nav_settings -> {
                // Handle the settings action
                Toast.makeText(this, "Change the time & interval",
                    Toast.LENGTH_SHORT).show()
                pauseTimers()
                replaceFragment(SetTimeFragment(), menuItem.title.toString())
            }
            R.id.nav_reset -> {
                // Handle the reset action
                reset()
            }
            R.id.pause -> {
                pauseTimers()
            }
            R.id.about -> {
                replaceFragment(AboutFragment(), menuItem.title.toString())
                pauseTimers()
            }
        }
        // Close the navigation drawer after item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private fun pauseTimers() {
        isPaused = true
        timerSelected = if (clock1Running) {
            1
        } else {
            2
        }
        timer1.cancel()
        timer2.cancel()
    }

    // opens and closes the drawer when icon is clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            true

        } else super.onOptionsItemSelected(item)
    }

    // handles dark mode toggle in nav menu (isCheckedState true = dark mode)
    private fun toggleDarkMode(isCheckedState: Boolean) {
        // If isCheckedState is true, we want dark mode
        if (isCheckedState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            saveDarkModePreference(true)
            Toast.makeText(this, "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
        } else {
            // If isCheckedState is false, we want light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            saveDarkModePreference(false)
            Toast.makeText(this, "Switched to Light Mode", Toast.LENGTH_SHORT).show()
        }

        // Update the switch state to reflect the current mode
        isListenerEnabled = false // Disable listener temporarily
        switchDarkMode.isChecked = isCheckedState // Set the switch state
        isListenerEnabled = true // Re-enable listener
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    // save the users preference for dark or light mode
    private fun saveDarkModePreference(isDarkMode: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()
    }

    // Timer object for player 1, 600000 = 10 minutes 1 = 1 millisecond
    private val timer1 = object : CountDownTimer(clock1Time, 1000) {
        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {
            clock1Running = true
            clock1Time -= 1000
            setClockText(clock1, clock1Time)
        }

        // displays message when the time runs out
        override fun onFinish() {
            clock1Running = false
            clock1.text = getString(R.string.timeOut)
        }
    }

    // Timer object for player 2
    private val timer2 = object : CountDownTimer(clock2Time, 1000) {
        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {
            clock2Running = true
            clock2Time -= 1000
            setClockText(clock2, clock2Time)
        }
        // displays message when the time runs out
        override fun onFinish() {
            clock2Running = false
            clock2.text = getString(R.string.timeOut)
        }
    }

    // method for capturing button click to swap clocks
    fun timerSwapButtonClick(view: View?) {
        println("isPaused: $isPaused")
        println("Clock1Running: $clock1Running")
        println("Clock2Running: $clock2Running")
        println("timerSelected: $timerSelected")
        println("interval: $interval")
        println("view: $view")
        if (isPaused) {
            if (timerSelected == 1) {
                clock1Running = true
                setClockText(clock2, clock2Time)
                isPaused = false
            } else {
                clock2Running = false
                setClockText(clock1, clock1Time)
                isPaused = false
            }
        } else {
            if (timerSelected == 1) {
                // adds & displays extra 5 seconds when clock is stopped per Fischer rules
                clock1Running = false
                clock1Time += interval
                setClockText(clock1, clock1Time)
                timerSelected = 2
            } else {
                // adds & displays extra 5 seconds when clock is stopped per Fischer rules
                clock2Running = false
                clock2Time += interval
                setClockText(clock2, clock2Time)
                timerSelected = 1
            }
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

    // saves the current clock values between instance states
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the current value of each clock
        outState.putLong("clock1Time", clock1Time)
        outState.putLong("clock2Time", clock2Time)
        outState.putLong("interval_time", interval)
        outState.putInt("timer_selected", timerSelected)
    }

    // handles switching between menu fragments
    private fun replaceFragment(fragment: Fragment, title: String) {
        // log entry for troubleshooting
        Log.d("MainActivity", "Replacing fragment with: $title")
        // do stuff when menu item is selected
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentView, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(title)
    }
    // Method to set the Switch state without triggering the listener, false turns it off
    private fun switchListenerToggle(isChecked: Boolean) {
        isListenerEnabled = false // Disable the listener
        switchDarkMode.isChecked = isChecked // Set the checked state
        isListenerEnabled = true // Re-enable the listener
    }

    private fun reset() {
        Toast.makeText(this, "Time reset.", Toast.LENGTH_SHORT).show()
        timer1.cancel()
        timer2.cancel()
        // restore original saved values when reset is pressed
        val defaultTime = 600000.toLong()
        //sharedPrefs.edit().putLong("clock_time", defaultTime).apply()
        //sharedPrefs.edit().putLong("interval_time", 5000).apply()
        timerSelected = 1
        clock1Time = sharedPrefs.getLong("clock_time", defaultTime)
        clock2Time = sharedPrefs.getLong("clock_time", defaultTime)
        startDialog(timer1, timer2, timerSelected)
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