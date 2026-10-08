package com.example.myno.msos6.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myno.msos6.utils.VersionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class UpdateManager(private val context: Context) {

    private val localVersion = getLocalVersionName()
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun checkUpdate(isManual: Boolean = false) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/jiasas888/MSKOS/releases")
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "MSKOS-Update-Checker")
                .build()

            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${response.message}")
            }

            val body = response.body?.string() ?: throw Exception("响应为空")
            val jsonArray = JSONArray(body)

            var latestStableTag: String? = null
            var latestStableTime: String? = null
            var latestStableUrl: String? = null
            var latestStableHtmlUrl: String? = null

            var latestBetaTag: String? = null
            var latestBetaTime: String? = null
            var latestBetaUrl: String? = null
            var latestBetaHtmlUrl: String? = null

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val prerelease = obj.optBoolean("prerelease", false)
                val tag = obj.optString("tag_name", "")
                val time = obj.optString("published_at", "")
                val htmlUrl = obj.optString("html_url", "")

                // 取第一个 asset 的下载链接
                val assets = obj.optJSONArray("assets")
                val downloadUrl = if (assets != null && assets.length() > 0) {
                    assets.getJSONObject(0).optString("browser_download_url", "")
                } else {
                    ""
                }

                if (!prerelease && latestStableTag == null) {
                    latestStableTag = tag
                    latestStableTime = time
                    latestStableUrl = downloadUrl
                    latestStableHtmlUrl = htmlUrl
                }
                if (prerelease && latestBetaTag == null) {
                    latestBetaTag = tag
                    latestBetaTime = time
                    latestBetaUrl = downloadUrl
                    latestBetaHtmlUrl = htmlUrl
                }
            }

            val stableHigher = latestStableTag != null &&
                    VersionUtils.compareVersion(
                        VersionUtils.parseVersion(latestStableTag), localVersion
                    ) > 0
            val betaHigher = latestBetaTag != null &&
                    VersionUtils.compareVersion(
                        VersionUtils.parseVersion(latestBetaTag), localVersion
                    ) > 0

            if (stableHigher || betaHigher) {
                withContext(Dispatchers.Main) {
                    try {
                        showUpdateDialog(
                            latestStableTag, latestStableTime,
                            latestStableUrl ?: latestStableHtmlUrl ?: "",
                            stableHigher,
                            latestBetaTag, latestBetaTime,
                            latestBetaUrl ?: latestBetaHtmlUrl ?: "",
                            betaHigher
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (isManual) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (isManual) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "检查失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showUpdateDialog(
        stableTag: String?, stableTime: String?, stableUrl: String, stableHigher: Boolean,
        betaTag: String?, betaTime: String?, betaUrl: String, betaHigher: Boolean
    ) {
        val sb = StringBuilder()
        if (stableHigher && stableTag != null) {
            sb.append("正式版: $stableTag\n")
            sb.append("发布时间: ${formatTime(stableTime ?: "")}\n")
            if (betaHigher && betaTag != null) sb.append("\n")
        }
        if (betaHigher && betaTag != null) {
            sb.append("测试版: $betaTag\n")
            sb.append("发布时间: ${formatTime(betaTime ?: "")}")
        }

        val downloadUrl = if (stableHigher) stableUrl else betaUrl

        if (context is AppCompatActivity && !context.isFinishing && !context.isDestroyed) {
            AlertDialog.Builder(context)
                .setTitle("发现新版本")
                .setMessage(sb.toString())
                .setCancelable(true)
                .setPositiveButton("立即更新") { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                .setNegativeButton("稍后再说", null)
                .show()
        }
    }

    private fun formatTime(isoTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(isoTime)
            date?.let { outputFormat.format(it) } ?: isoTime
        } catch (e: Exception) {
            isoTime
        }
    }

    private fun getLocalVersionName(): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            info.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}