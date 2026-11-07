package com.example.myno.msos6

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var rootView: View
    private lateinit var previewContainer: FrameLayout
    private lateinit var previewText: TextView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSelectBgColor: Button
    private lateinit var btnReset: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var currentBgImagePath: String? = null

    // -1 表示未自定义，背景跟随主题
    private var currentBgColor: Int = SettingManager.NO_COLOR

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val imageUri = data?.data
            if (imageUri != null) {
                handleImageResult(imageUri)
            } else {
                Toast.makeText(this, "未选择图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_zhuti)

        // 和 xtmain 一致的平板缩放逻辑
        rootView = findViewById<View>(android.R.id.content)
        ScreenUtils.applyScale(this)

        if (SettingManager.isActivityAffected("SettingsActivity")) {
            SettingManager.applySettings(this, rootView)
        }

        sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)

        initViews()
        setupListeners()
        restoreState()
    }

    override fun onResume() {
        super.onResume()
        if (SettingManager.isActivityAffected("SettingsActivity")) {
            SettingManager.applySettings(this, rootView)
        }
    }

    private fun initViews() {
        previewContainer = findViewById(R.id.previewContainer)
        previewText = findViewById(R.id.previewText)
        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSelectBgColor = findViewById(R.id.btnSelectBgColor)
        btnReset = findViewById(R.id.btnReset)

        // 预览文字用主题的次要文字色，自动跟随日夜
        previewText.text = "预览区域"
        previewText.setTextColor(resolveThemeColor(android.R.attr.textColorSecondary))
    }

    /**
     * 解析主题属性对应的颜色，自动跟随深色模式
     */
    private fun resolveThemeColor(attr: Int): Int {
        val tv = TypedValue()
        val found = theme.resolveAttribute(attr, tv, true)
        return if (found) tv.data else Color.GRAY
    }

    private fun setupListeners() {
        btnSelectImage.setOnClickListener {
            openGallery()
        }

        btnSelectBgColor.setOnClickListener {
            showCustomColorPicker("选择背景色")
        }

        btnReset.setOnClickListener {
            resetSettings()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun showCustomColorPicker(title: String) {
        val colorItems = arrayOf(
            ColorItem("白色", "#FFFFFF"),
            ColorItem("黄色", "#FFEB3B"),
            ColorItem("绿色", "#4CAF50"),
            ColorItem("蓝色", "#2196F3"),
            ColorItem("红色", "#F44336"),
            ColorItem("紫色", "#9C27B0"),
            ColorItem("橙色", "#FF9800"),
            ColorItem("棕色", "#795548"),
            ColorItem("灰色", "#607D8B"),
            ColorItem("黑色", "#000000")
        )

        val colorNames = colorItems.map { it.name }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(colorNames) { _, which ->
                val color = Color.parseColor(colorItems[which].hex)
                handleColorResult(color)
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun handleImageResult(imageUri: Uri) {
        try {
            val imagePath = SettingManager.saveImageToLocal(this, imageUri)

            if (imagePath != null) {
                currentBgImagePath = imagePath
                // 有图片时颜色失效
                currentBgColor = SettingManager.NO_COLOR

                val bitmap = SettingManager.loadImageFromLocal(this, imagePath)
                if (bitmap != null) {
                    val drawable = BitmapDrawable(resources, bitmap)
                    previewContainer.background = drawable
                    Toast.makeText(this, "背景图片已保存", Toast.LENGTH_SHORT).show()
                    saveState()
                } else {
                    Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "加载图片失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleColorResult(color: Int) {
        // 选了颜色 → 清掉图片
        if (currentBgImagePath != null) {
            SettingManager.clearBgImage(this)
            currentBgImagePath = null
        }

        previewContainer.setBackgroundColor(color)
        currentBgColor = color
        Toast.makeText(this, "背景色已更新", Toast.LENGTH_SHORT).show()
        saveState()
    }

    private fun resetSettings() {
        // 清图
        if (currentBgImagePath != null) {
            SettingManager.clearBgImage(this)
            currentBgImagePath = null
        }

        // 恢复为「未自定义」→ 跟随主题
        currentBgColor = SettingManager.NO_COLOR

        previewContainer.setBackgroundColor(
            SettingManager.resolveThemeColorBackground(this)
        )

        Toast.makeText(this, "已重置所有设置", Toast.LENGTH_SHORT).show()
        saveState()
    }

    private fun saveState() {
        sharedPreferences.edit().apply {
            putString("bg_image_path", currentBgImagePath)

            if (currentBgColor == SettingManager.NO_COLOR) {
                remove("bg_color")
            } else {
                putInt("bg_color", currentBgColor)
            }
            apply()
        }
        sendBroadcast(Intent("SETTINGS_UPDATED"))
    }

    private fun restoreState() {
        // 默认 NO_COLOR 表示未自定义
        val bgColor = sharedPreferences.getInt("bg_color", SettingManager.NO_COLOR)
        currentBgColor = bgColor

        val imagePath = sharedPreferences.getString("bg_image_path", null)

        if (!imagePath.isNullOrEmpty()) {
            currentBgImagePath = imagePath
            val bitmap = SettingManager.loadImageFromLocal(this, imagePath)
            if (bitmap != null) {
                val drawable = BitmapDrawable(resources, bitmap)
                previewContainer.background = drawable
            } else {
                // 图片加载失败，清理并回退到颜色/主题
                currentBgImagePath = null
                sharedPreferences.edit().remove("bg_image_path").apply()
                applyColorOrTheme(bgColor)
            }
        } else {
            applyColorOrTheme(bgColor)
        }
    }

    /**
     * 根据是否自定义颜色决定预览背景
     */
    private fun applyColorOrTheme(bgColor: Int) {
        if (bgColor == SettingManager.NO_COLOR) {
            previewContainer.setBackgroundColor(
                SettingManager.resolveThemeColorBackground(this)
            )
        } else {
            previewContainer.setBackgroundColor(bgColor)
        }
    }

    data class ColorItem(val name: String, val hex: String)
}