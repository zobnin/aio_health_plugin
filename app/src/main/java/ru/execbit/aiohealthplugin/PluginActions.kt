package ru.execbit.aiohealthplugin

import android.content.Context
import android.content.Intent
import ru.execbit.aiolauncher.models.PluginAction
import ru.execbit.aiolauncher.models.PluginError
import ru.execbit.aiolauncher.models.PluginResult
import ru.execbit.aiolauncher.plugin.sendPluginResult

class PluginActions(private val context: Context) {
    fun processAction(state: State, intent: Intent) {
        try {
            intent.getParcelableExtraCompat<PluginAction>("action")?.let { action ->
                when (action.context) {
                    "tap" -> processTapAction(state)
                    else -> { /* ignore */ }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processTapAction(state: State) {
        when (state) {
            is State.DataAvailable -> context.openFitnessApp(pluginComponent)
            is State.NoPermission -> context.openMainActivity(pluginComponent)
            is State.NotAvailable -> context.openAppStoreHealth()
            else -> {}
        }
    }

    private fun sendError() {
        val result = PluginResult(
            from = pluginComponent,
            data = PluginError(INVALID_ACTION_ERROR, context.getString(R.string.invalid_action))
        )
        context.sendPluginResult(result)
    }
}