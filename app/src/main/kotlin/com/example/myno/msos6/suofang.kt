package com.example.myno.msos6

import android.view.View
import androidx.appcompat.app.AppCompatActivity

object ScreenUtils {

    private const val DESIGN_WIDTH = 360f
    private const val TABLET_THRESHOLD = 600f
    private const val MAX_SCALE = 1.75f

    fun applyScale(activity: AppCompatActivity) {
        val metrics = activity.resources.displayMetrics
        val screenWidth = metrics.widthPixels / metrics.density

        val scale = if (screenWidth > TABLET_THRESHOLD) {
            minOf(screenWidth / DESIGN_WIDTH, MAX_SCALE)
        } else {
            1.0f
        }

        val rootView = activity.findViewById<View>(android.R.id.content)
        rootView.scaleX = scale
        rootView.scaleY = scale
    }
}