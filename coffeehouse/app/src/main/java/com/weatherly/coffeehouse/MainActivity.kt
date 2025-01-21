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
 */
package com.weatherly.coffeehouse

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
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.DecimalFormat
import com.weatherly.coffeehouse.SetTimeFragment

class MainActivity : AppCompatActivity() {

    // user's selection of which player's turn it is
    private var timerSelected = 1
    // keep track of which clock is running
    var clock1Running = false
    var clock2Running = false
    // remaining time on each clock
    var clock1Time: Long = 600000
    var clock2Time: Long = 600000
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

    // runs on creation of the main activity, at app start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //check user's dark/light mode preference
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", true)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // assigning ID of the toolbar to a variable
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // uses the toolbar as an actionbar
        setSupportActionBar(toolbar)

        // assign the clock view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)

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
        // handles navigationView menu item selection
        mainNavigationView.setNavigationItemSelectedListener { menuItem ->
            onNavigationItemSelected(menuItem)
        }



        // if saved state exists, retrieve clock times before proceeding
        if (savedInstanceState != null) {
            // Restore the state from the saved key values
            clock1Time = savedInstanceState.getLong("clock1TimeKey")
            clock2Time = savedInstanceState.getLong("clock2TimeKey")
            setClockText(clock1, clock1Time)
            setClockText(clock2, clock2Time)
        }

        // Displays start message to user, starts countdown when clicked
        startDialog(timer1, timer2, timerSelected)
    }

    // function for starting message
    private fun startDialog(timer1:CountDownTimer, timer2:CountDownTimer, timerSelected: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)

        val body: TextView = dialog.findViewById(R.id.intro_card)
        body.text = getString(R.string.intro_message)
        // set clocks before message is displayed
        setClockText(clock1, clock1Time)
        setClockText(clock2, clock2Time)

        // closes the dialog when user clicks outside of it
        dialog.setCanceledOnTouchOutside(true)
        // do something on touch
        dialog.show()

        dialog.window?.decorView?.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dialog.dismiss()
                view.performClick() // calling performClick as per accessibility suggestions
                // begin the countdown!!!
                startTimers(timer1, timer2, timerSelected)
                true // Indicate that the touch event was handled
            } else {
                false
            }
        }
    }

    private fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Log.d("MainActivity", "Menu item selected: ${menuItem.title}")
        when (val id = menuItem.itemId) {
            R.id.nav_dark_mode -> {
                // Handle the theme switch action
                Toast.makeText(this, "Theme changed", Toast.LENGTH_SHORT).show()
                toggleDarkMode(menuItem)
            }
            R.id.nav_settings -> {
                // Handle the settings action
                Toast.makeText(this, "Settings menu coming soon!", Toast.LENGTH_SHORT).show()
                pauseTimers()
                replaceFragment(SetTimeFragment(), menuItem.title.toString())

            }
            R.id.nav_reset -> {
                // Handle the reset action
                Toast.makeText(this, "Time reset.", Toast.LENGTH_SHORT).show()
                timer1.cancel()
                timer2.cancel()
                clock1Time = 600000
                clock2Time = 600000
                startDialog(timer1, timer2, timerSelected)
            }
            R.id.pause -> {
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

    // handles dark mode toggle in nav menu
    private fun toggleDarkMode(menuItem: MenuItem) {
        val mainNavigationView = findViewById<View>(R.id.navigation) as NavigationView
        val navItem = mainNavigationView.menu.findItem(R.id.nav_dark_mode)
        val actionView = navItem.actionView
        if (actionView != null) {
            switchDarkMode = actionView.findViewById(R.id.switch_dark_mode)
        }

        // Get the current night mode
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            // If currently in dark mode, switch to light mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            saveDarkModePreference(false)
            switchDarkMode.setChecked(true)
            Toast.makeText(this, "Switched to Light Mode", Toast.LENGTH_SHORT).show()
        } else {
            // If currently in light mode, switch to dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            saveDarkModePreference(true)
            switchDarkMode.setChecked(false)
            Toast.makeText(this, "Switched to Dark Mode", Toast.LENGTH_SHORT).show()
        }
    }

    // save the users preference for dark or light mode
    private fun saveDarkModePreference(isDarkMode: Boolean) {
        val sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
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
                clock1Time += 5000
                setClockText(clock1, clock1Time)
                timerSelected = 2
            } else {
                // adds & displays extra 5 seconds when clock is stopped per Fischer rules
                clock2Running = false
                clock2Time += 5000
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
        outState.putLong("clock1TimeKey", clock1Time)
        outState.putLong("clock2TimeKey", clock2Time)
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