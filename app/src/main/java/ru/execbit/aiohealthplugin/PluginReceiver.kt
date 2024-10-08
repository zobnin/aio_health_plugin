package ru.execbit.aiohealthplugin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.execbit.aiolauncher.models.PluginIntentActions
import ru.execbit.aiolauncher.plugin.checkAioVersion
import ru.execbit.aiolauncher.plugin.checkUid
import ru.execbit.aiolauncher.plugin.sendInvalidAioVersionError
import ru.execbit.aiolauncher.plugin.sendString

class PluginReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            if (!checkUid(intent)) {
                return
            }

            if (!checkAioVersion(context, REQUIRED_AIO_VERSION)) {
                context.sendInvalidAioVersionError(pluginComponent)
                return
            }

            when {
                // All work should be done in the service
                context.isServiceRunning(PluginService::class.java) -> {
                    PluginService.startService(context, intent)
                }
                // If service is not working show "Tap to enable" to
                // launch plugin and start service
                intent.action == PluginIntentActions.PLUGIN_GET_DATA -> {
                    updateState(State.ServiceNotRunning)
                    context.sendString(
                        cn = pluginComponent,
                        string = context.getString(R.string.tap_to_enable),
                        private = false
                    )
                }
                // There can be only one action if service is not running:
                // "Tap to enable" button
                intent.action == PluginIntentActions.PLUGIN_SEND_ACTION -> {
                    context.openMainActivity(pluginComponent)
                }
            }
        }
    }
}
