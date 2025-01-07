/*
Main Activity file for Chess Clock Android App
Author:  Chip Weatherly
Date:    1/6/2025
Purpose: Holds logic for chess clock app main screen
 */
package com.example.coffeehouse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer
import android.widget.TextView
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    // textView holds id of the first textView item
    lateinit var clock1 : TextView
    lateinit var clock2 : TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // assign the view ids to variables
        clock1 = findViewById(R.id.clock1)
        clock2 = findViewById(R.id.clock2)

        // Timer object, 600000 = 10 mins 1 = 1 millisecond
        object : CountDownTimer(600000, 1000) {

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
                clock1.text = "Time's Up!"
            }
        }.start()

        object : CountDownTimer(600000, 1000) {

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
                clock2.text = "Time's Up!"
            }
        }.start()
    }
}