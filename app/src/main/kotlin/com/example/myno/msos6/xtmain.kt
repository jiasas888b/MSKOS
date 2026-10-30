package com.example.myno.msos6

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.myno.msos6.SettingmainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class xtmain : AppCompatActivity() {

    private lateinit var rootView: View

    private val appMap = mapOf(
        "微信" to "com.tencent.mm",
        "wechat" to "com.tencent.mm",
        "WeChat" to "com.tencent.mm",
        "QQ" to "com.tencent.mobileqq",
        "qq" to "com.tencent.mobileqq"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xt_active)

        val designWidth = 360f
        val screenWidth = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val scale = minOf(screenWidth / designWidth, 1.75f)

        rootView = findViewById<View>(android.R.id.content)
        rootView.scaleX = scale
        rootView.scaleY = scale

        if (SettingManager.isActivityAffected("xtmain")) {
            SettingManager.applySettings(this, rootView)
        }

        val backButton = findViewById<Button>(R.id.click)
        backButton.setOnClickListener {
            finish()
        }

        val datebtn = findViewById<ImageButton>(R.id.date_app)
        datebtn.setOnClickListener {
            val intent = Intent(this, DateCo::class.java)
            startActivity(intent)
        }

        val calbtn = findViewById<ImageButton>(R.id.cal_app)
        calbtn.setOnClickListener {
            val intent = Intent(this, calculator::class.java)
            startActivity(intent)
        }

        val setbtn = findViewById<ImageButton>(R.id.settings)
        setbtn.setOnClickListener {
            val intent = Intent(this, SettingmainActivity::class.java)
            startActivity(intent)
        }

        val etInput = findViewById<EditText>(R.id.etInput)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val inputText = etInput.text.toString().trim()

            val packageName = appMap[inputText]
                ?: appMap[inputText.lowercase()]
                ?: appMap[inputText.uppercase()]

            if (packageName != null) {
                try {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    if (intent != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "未找到该应用", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show()
                }
            } else {
                when (inputText) {
                    "你好" -> {
                        Toast.makeText(this, "你好", Toast.LENGTH_SHORT).show()
                    }
                    "再见" -> {
                        Toast.makeText(this, "再见", Toast.LENGTH_SHORT).show()
                    }
                    "时间" -> {
                        val currentTime = getCurrentTime()
                        Toast.makeText(this, "当前时间：$currentTime", Toast.LENGTH_SHORT).show()
                    }
                    "系统" -> {
                        Toast.makeText(this, "MSKOS 1", Toast.LENGTH_SHORT).show()
                    }
                    "日历" -> {
                        val intent = Intent(this, DateCo::class.java)
                        startActivity(intent)
                    }
                    "计算器" -> {
                        val intent = Intent(this, calculator::class.java)
                        startActivity(intent)
                    }
                    "MSOS" -> {
                        AlertDialog.Builder(this)
                            .setMessage("是当前系统前身")
                            .setPositiveButton("确定", null)
                            .show()
                    }
                    else -> {
                        Toast.makeText(this, "搜索失败：无此关键词", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val mscIcon = findViewById<ImageButton>(R.id.msc_launchicon)
        if (Build.VERSION.SDK_INT >= 28) {
            mscIcon.isEnabled = true
            mscIcon.setOnClickListener {
                try {
                    val intent = packageManager.getLaunchIntentForPackage("com.MSS.MSC")
                    if (intent != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "未找到该应用", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            mscIcon.isEnabled = false
            mscIcon.setOnClickListener {
                Toast.makeText(this, "游戏需要安卓9.0以上系统", Toast.LENGTH_SHORT).show()
            }
        }

        val msrIcon = findViewById<ImageButton>(R.id.msr_launchicon)
        if (Build.VERSION.SDK_INT >= 26) {
            msrIcon.isEnabled = true
            msrIcon.setOnClickListener {
                try {
                    val intent_msr = packageManager.getLaunchIntentForPackage("com.MSS.MSQR")
                    if (intent_msr != null) {
                        startActivity(intent_msr)
                    } else {
                        Toast.makeText(this, "未找到该应用", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            msrIcon.isEnabled = false
            msrIcon.setOnClickListener {
                Toast.makeText(this, "游戏需要安卓8.0以上系统", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingManager.isActivityAffected("xtmain")) {
            SettingManager.applySettings(this, rootView)
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}