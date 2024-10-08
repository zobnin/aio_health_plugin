package ru.execbit.aiohealthplugin

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import ru.execbit.preferences.Preference

class App: Application() {
    companion object {
        // Holding app context is not a memory leak
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        Preference.init(context)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = name
        }

        (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.apply {
            createNotificationChannel(channel)
        }
    }
}