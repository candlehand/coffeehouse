/*
Main Activity file for Chess Clock Android App
Author:  Chip Weatherly
Date:    1/6/2025
Purpose: Holds logic for chess clock app main screen
Changelog:
1/6/25 Initial creation - CW
1/7/25 Added clock functionality - CW
1/8/25 Added ability to switch between clocks by clicking the screen - CW
 */
package com.example.coffeehouse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    // hold user's selection of which player's turn it is
    private var timerSelected = 1
    // holds remaining time on each clock
    private var clock1Time: Long = 600000
    private var clock2Time: Long = 600000
    // init textView variables for the 2 clocks
    lateinit var clock1 : TextView
    lateinit var clock2 : TextView
    // runs on creation of the main activity, at app start
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // assign the view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)
        // assign initial values to clock text. To be changed into variables
        clock1.text = getString(R.string.initialClock)
        clock2.text = getString(R.string.initialClock)
        // begin the countdown!!!
        startTimers(timer1, timer2, timerSelected)
    }
    // Timer object for player 1, 600000 = 10 minutes 1 = 1 millisecond
    private val timer1 = object : CountDownTimer(clock1Time, 1000) {
        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {
            clock1Time -= 1000
            val dec = DecimalFormat("00")
            val hour = (clock1Time / 3600000) % 24
            val min = (clock1Time / 60000) % 60
            val sec = (clock1Time / 1000) % 60
            clock1.text = dec.format(hour) + ":" + dec.format(min) + ":" + dec.format(sec)
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
            val dec = DecimalFormat("00")
            val hour = (clock2Time / 3600000) % 24
            val min = (clock2Time / 60000) % 60
            val sec = (clock2Time / 1000) % 60
            clock2.text = dec.format(hour) + ":" + dec.format(min) + ":" + dec.format(sec)
        }
        // displays message when the time runs out
        override fun onFinish() {
            clock2.text = getString(R.string.timeOut)
        }
    }
    // method for capturing button click to swap clocks
    fun timerSwapButtonClick(view: View?) {
        // save the current clock numbers when clicked
        println(clock1Time)
        timerSelected = if (timerSelected == 1){
            2
        } else {
            1
        }
        startTimers(timer1, timer2, timerSelected)
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