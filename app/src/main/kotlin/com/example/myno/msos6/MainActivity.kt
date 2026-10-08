package com.example.myno.msos6

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = true
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val designWidth = 360f
        val screenWidth = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val scale = minOf(screenWidth / designWidth, 1.75f)

        rootView = findViewById<View>(android.R.id.content)
        rootView.scaleX = scale
        rootView.scaleY = scale

        val tvCurrentTime = findViewById<TextView>(R.id.tvCurrentTime)
        startUpdatingTime(tvCurrentTime)

        if (SettingManager.isActivityAffected("MainActivity")) {
            SettingManager.applySettings(this, rootView)
        }

        val navigateButton = findViewById<Button>(R.id.navigate_button)
        navigateButton.text = "启动系统"
        navigateButton.setOnClickListener {
            val intent = Intent(this, xtmain::class.java)
            startActivity(intent)
        }

        val exitButton = findViewById<Button>(R.id.exit_button)
        exitButton.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingManager.isActivityAffected("MainActivity")) {
            SettingManager.applySettings(this, rootView)
        }
    }

    private fun startUpdatingTime(textView: TextView) {
        val updateRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    val currentTime = getCurrentTime()
                    textView.text = currentTime
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(updateRunnable)
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}