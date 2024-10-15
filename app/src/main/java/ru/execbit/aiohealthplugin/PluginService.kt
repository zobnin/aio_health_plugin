package ru.execbit.aiohealthplugin

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ru.execbit.aiolauncher.models.PluginIntentActions
import ru.execbit.aiolauncher.plugin.Updater

/*
 * In fact, this service is not needed, the plugin itself does not need it.
 * But there is one but: Health Connect does not give data to applications that are running in the background.
 * ForegroundService is not considered a foreground component, so we use it.
 */

class PluginService : Service() {
    companion object {
        private val actions by lazy { PluginActions(App.context) }
        private val data by lazy { PluginData(App.context) }

        fun startService(context: Context, intent: Intent?) {
            val serviceIntent = Intent(context, PluginService::class.java)
            if (intent != null) {
                serviceIntent.putExtra(INTENT_ACTION, intent.action)
                serviceIntent.putExtras(intent)
            }
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        if (intent != null) {
            if (intent.getStringExtra(INTENT_ACTION) == null) {
                return START_STICKY
            }

            when (intent.getStringExtra(INTENT_ACTION)) {
                PluginIntentActions.PLUGIN_GET_DATA -> data.processGetData(intent)
                PluginIntentActions.PLUGIN_SEND_ACTION -> actions.processAction(state.value, intent)
            }
        }

        Updater.checkForNewVersionAndShowNotify(applicationContext)

        return START_STICKY
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notify_message))
            .setSmallIcon(R.drawable.ic_heart_24)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
