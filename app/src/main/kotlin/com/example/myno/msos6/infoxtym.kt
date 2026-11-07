package com.example.myno.msos6

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

class infoxtym : AppCompatActivity() {

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.infoxtv)  // 用你原来的布局

        rootView = findViewById<View>(android.R.id.content)
        ScreenUtils.applyScale(this)

        // 你原来的按钮
        val msosButton = findViewById<Button>(R.id.msos_ver)
        val versionButton = findViewById<Button>(R.id.version)
        val dateButton = findViewById<Button>(R.id.date)
        
        val button = findViewById<Button>(R.id.update_button)
		button.setOnClickListener {
    		val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jiasas888b/MSKOS/releases"))
    			startActivity(intent)
		}

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun backtoast() {
        Toast.makeText(this, "返回", Toast.LENGTH_SHORT).show()
    }
}