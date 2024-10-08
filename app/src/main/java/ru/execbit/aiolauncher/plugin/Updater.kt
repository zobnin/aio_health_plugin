package ru.execbit.aiolauncher.plugin

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.execbit.aiohealthplugin.App
import ru.execbit.aiohealthplugin.R
import ru.execbit.aiohealthplugin.Settings
import java.util.*

object Updater {
    data class PluginMeta(
        val name: String,
        val version: String,
        val versionCode: Int
    )

    private const val REPO_URL = "https://aiolauncher.app/scripts/"
    private val PKG_URL = REPO_URL + App.context.packageName + ".apk"
    private val META_URL = REPO_URL + App.context.packageName + ".meta"

    fun checkForNewVersionAndShowNotify(context: Context) {
        val time = Date().time

        if (Settings.lastPluginUpdateCheck == 0L) {
            Settings.lastPluginUpdateCheck = time
            return
        }

        // Check once a day
        if (Settings.lastPluginUpdateCheck + 86400000 < time) {
            val meta = getPluginMeta()

            if (meta == null) {
                // Try again in an hour
                Settings.lastPluginUpdateCheck += 3600000
            } else {
                Settings.lastPluginUpdateCheck = time

                if (Settings.notifyShowedForVersion < meta.versionCode) {
                    showNotification(context, meta)
                    Settings.notifyShowedForVersion = meta.versionCode
                }
            }
        }
    }

    private fun getPluginMeta(): PluginMeta? {
        try {
            val request = Request.Builder()
                .url(META_URL)
                .build()

            val okHttpClient = OkHttpClient.Builder()
                .retryOnConnectionFailure(false)
                .build()

            okHttpClient.newCall(request).execute().body?.let {
                return parseJson(it.string())
            }

            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun showNotification(context: Context, meta: PluginMeta) {
        val browserIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(PKG_URL)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, browserIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context)
            .setContentTitle(context.getString(R.string.new_version_available, meta.version))
            .setContentText(context.getString(R.string.click_to_download))
            .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
            .setContentIntent(pendingIntent)
            .setChannelId("main")
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0, notification)
    }

    private fun parseJson(jsonStr: String): PluginMeta? {
        return try {
            val jsonObj = JSONObject(jsonStr)

            PluginMeta(
                name = jsonObj.getString("name"),
                version = jsonObj.getString("version"),
                versionCode = jsonObj.getInt("versionCode")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}