/*
Main Activity file for Chess Clock Android App
Author:  Chip Weatherly
Date:    1/6/2025
Purpose: Holds logic for chess clock app main screen
Changelog:
1/6/25 Initial creation - CW
1/7/25 Added clock functionality - CW
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

    // holds user selection of which player's turn it is'
    var timerSelected = 1

    // inits textView variables for the 2 clocks
    lateinit var clock1 : TextView
    lateinit var clock2 : TextView

    // Timer object, 600000 = 10 mins 1 = 1 millisecond
    val timer1 = object : CountDownTimer(600000, 1000) {

        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {

            val dec = DecimalFormat("00")

            val hour = (millisUntilFinished / 3600000) % 24
            val min = (millisUntilFinished / 60000) % 60
            val sec = (millisUntilFinished / 1000) % 60
            clock1.text = dec.format(hour) + ":" + dec.format(min) + ":" + dec.format(sec)
        }

        // fires when the time is up
        override fun onFinish() {
            clock1.text = getString(R.string.timeOut)
        }
    }

    val timer2 = object : CountDownTimer(600000, 1000) {

        // Callback function triggered regularly
        override fun onTick(millisUntilFinished: Long) {

            val dec = DecimalFormat("00")

            val hour = (millisUntilFinished / 3600000) % 24
            val min = (millisUntilFinished / 60000) % 60
            val sec = (millisUntilFinished / 1000) % 60
            clock2.text = dec.format(hour) + ":" + dec.format(min) + ":" + dec.format(sec)
        }

        // fires when the time is up
        override fun onFinish() {
            clock2.text = getString(R.string.timeOut)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // assign the view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)
        // assign initial values to clock text. To be changed into variables
        clock1.text = "10:00:00"
        clock2.text = "10:00:00"
        // begin the countdown!!!
        timerInit(timer1, timer2, timerSelected)
    }
    // method for capturing button click to swap clocks
    fun timerSwapButtonClick(view: View?) {
        timerSelected = if (timerSelected == 1){
            2
        } else {
            1
        }
        timerInit(timer1, timer2, timerSelected)
    }
}



// method initializes one timer at a time at program start
fun timerInit(timer1:CountDownTimer, timer2:CountDownTimer, timerSelected: Int) {
    // 'timerSelected' will hold the user's choice of who will go first
    if (timerSelected == 1){
        timer2.cancel()
        timer1.start()
    } else {
        timer1.cancel()
        timer2.start()
    }

}