package com.example.coffeehouse

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.CountDownTimer
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var textView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.textView)

        // testing : (findViewById(R.id.main) -> (findViewById(R.id.textView)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.textView)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}


        // Timer object, 600000 = 10 mins 1 = 1 millisecond
        object : CountDownTimer(30000, 1000) {

            // Callback function triggered regularly
            override fun onTick(millisUntilFinished: Long) {
                textView.text = "seconds remaining: " + millisUntilFinished / 1000
            }

            // fires when the time is up
            override fun onFinish() {
                textView.text = "Done!"
            }
        }.start()
    }
}