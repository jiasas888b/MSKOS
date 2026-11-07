package com.example.myno.msos6

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myno.msos6.R
import com.example.myno.msos6.SettingsActivity

class SettingmainActivity : AppCompatActivity() {

    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        rootView = findViewById<View>(android.R.id.content)
        ScreenUtils.applyScale(this)

        if (SettingManager.isActivityAffected("SettingmainActivity")) {
            SettingManager.applySettings(this, rootView)
        }

        // shezhi
        val sheZhi: TextView = findViewById(R.id.name)
        sheZhi.text = "设置"

        // 主题按钮
        val btnZhuti: Button = findViewById(R.id.btnzhuti)
        btnZhuti.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val btnInf: Button = findViewById(R.id.btninf)
        btnInf.setOnClickListener {
            val inf = Intent(this, infoxtym::class.java)
            startActivity(inf)
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingManager.isActivityAffected("SettingmainActivity")) {
            SettingManager.applySettings(this, rootView)
        }
    }
}