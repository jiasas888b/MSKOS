package com.example.myno.msos6

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.TypedValue
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object SettingManager {
    private const val PREFS_NAME = "settings_prefs"
    private const val KEY_BG_IMAGE_PATH = "bg_image_path"
    private const val KEY_BG_COLOR = "bg_color"

    // 用 -1 表示「未自定义」，此时背景跟随主题（自动适配深色模式）
    const val NO_COLOR = -1

    private val affectedActivities = setOf(
        "MainActivity",
        "xtmain"
    )

    fun isActivityAffected(activityName: String): Boolean {
        return affectedActivities.contains(activityName)
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ---------- 图片相关 ----------

    fun saveImageToLocal(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap != null) {
                val dir = File(context.filesDir, "settings_images")
                if (!dir.exists()) {
                    dir.mkdirs()
                }

                val file = File(dir, "bg_image.jpg")
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.close()

                file.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadImageFromLocal(context: Context, path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                BitmapFactory.decodeFile(path)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ---------- 背景应用 ----------

    /**
     * 应用背景：
     * 1. 有自定义图片 → 用图片
     * 2. 有自定义颜色 → 用该颜色
     * 3. 都没有 → 用主题 colorBackground（自动跟随深色模式）
     */
    fun applyBackground(context: Context, view: View) {
        val prefs = getPrefs(context)
        val imagePath = prefs.getString(KEY_BG_IMAGE_PATH, null)
        val bgColor = prefs.getInt(KEY_BG_COLOR, NO_COLOR)

        // 1. 优先图片
        if (!imagePath.isNullOrEmpty()) {
            val bitmap = loadImageFromLocal(context, imagePath)
            if (bitmap != null) {
                view.background = BitmapDrawable(context.resources, bitmap)
                return
            } else {
                // 图片加载失败，清理残留记录，走后续逻辑兜底
                prefs.edit().remove(KEY_BG_IMAGE_PATH).apply()
            }
        }

        // 2. 用户自定义了颜色
        if (bgColor != NO_COLOR) {
            view.setBackgroundColor(bgColor)
            return
        }

        // 3. 未自定义 → 用主题背景色（自动适配深色模式）
        view.setBackgroundColor(resolveThemeColorBackground(context))
    }

    /**
     * 解析当前主题的 android:colorBackground 属性值
     * 这个值会随 DayNight 主题自动在浅色/深色之间切换
     */
    fun resolveThemeColorBackground(context: Context): Int {
        val tv = TypedValue()
        val found = context.theme.resolveAttribute(
            android.R.attr.colorBackground, tv, true
        )
        return if (found) tv.data else Color.WHITE
    }

    fun applySettings(context: Context, view: View) {
        applyBackground(context, view)
    }

    // ---------- 读取/写入 ----------

    /**
     * 返回用户自定义的背景色，未自定义时返回 NO_COLOR(-1)
     */
    fun getBgColor(context: Context): Int {
        return getPrefs(context).getInt(KEY_BG_COLOR, NO_COLOR)
    }

    fun getBgImagePath(context: Context): String? {
        return getPrefs(context).getString(KEY_BG_IMAGE_PATH, null)
    }

    /**
     * 保存自定义颜色。传 NO_COLOR 表示清除自定义，恢复跟随主题
     */
    fun saveBgColor(context: Context, color: Int) {
        getPrefs(context).edit().apply {
            if (color == NO_COLOR) {
                remove(KEY_BG_COLOR)
            } else {
                putInt(KEY_BG_COLOR, color)
            }
            apply()
        }
    }

    fun clearBgImage(context: Context) {
        val prefs = getPrefs(context)
        val imagePath = prefs.getString(KEY_BG_IMAGE_PATH, null)
        if (!imagePath.isNullOrEmpty()) {
            try {
                val file = File(imagePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        prefs.edit().remove(KEY_BG_IMAGE_PATH).apply()
    }

    /**
     * 重置所有设置：清图 + 清除自定义颜色 → 恢复跟随主题
     */
    fun resetSettings(context: Context) {
        clearBgImage(context)
        getPrefs(context).edit().apply {
            remove(KEY_BG_COLOR)
            apply()
        }
    }
}