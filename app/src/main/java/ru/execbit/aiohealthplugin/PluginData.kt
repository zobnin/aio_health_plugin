package ru.execbit.aiohealthplugin

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.execbit.aiolauncher.models.PluginLine
import ru.execbit.aiolauncher.models.PluginResult
import ru.execbit.aiolauncher.models.SearchPluginLines
import ru.execbit.aiolauncher.plugin.sendCardProgress
import ru.execbit.aiolauncher.plugin.sendPluginResult
import ru.execbit.aiolauncher.plugin.sendString

class PluginData(private val context: Context) {
    companion object {
        private const val NBSP = "&nbsp;"
        private const val ENSP = "&ensp;"
    }

    private val healthWrapper = HealthConnectWrapper()
    private val data = HealthData(healthWrapper)
    private val scope = CoroutineScope(Dispatchers.Main)

    fun processGetData(intent: Intent) {
        intent.getStringExtra("event")?.let { event ->
            when (event) {
                "load", "force", "resume" -> generateAndSendResult()
                "search" -> genAndSendSearchResult(intent)
                else -> return
            }
        }
    }

    private fun generateAndSendResult() {
        scope.launch(Dispatchers.IO) {
            dataToState()
            processState(context)
        }
    }

    private suspend fun dataToState() {
        try {
            if (data.isHealthConnectAvailable) {
                data.connect()
                updateState(State.Connected)

                if (data.checkPermissions()) {
                    updateState(State.Loading)
                    getData()
                } else {
                    updateState(State.NoPermission)
                }
            } else {
                updateState(State.NotAvailable)
            }
        } catch (e: Exception) {
            updateState(State.Error(e))
        }
    }

    private suspend fun getData() {
        try {
            val healthData = data.getHealthData()
            updateState(State.DataAvailable(healthData))
        } catch (e: NoSuchMethodError) {
            updateState(State.ReallyBadError)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processState(c: Context) {
        when (state.value) {
            is State.Error -> c.sendString(pluginComponent, (state.value as State.Error).exception.message.toString())
            is State.ReallyBadError -> c.sendString(pluginComponent, c.getString(R.string.error_unexpected))
            is State.NotConnected -> c.sendString(pluginComponent, c.getString(R.string.not_connected))
            is State.Connected -> c.sendString(pluginComponent, c.getString(R.string.connecting))
            is State.Loading -> c.sendString(pluginComponent, c.getString(R.string.loading))
            is State.DataAvailable -> sendHealthData(c, state.value as State.DataAvailable)
            is State.NoPermission -> c.sendString(pluginComponent, c.getString(R.string.tap_to_give_permission))
            is State.NotAvailable -> c.sendString(pluginComponent, c.getString(R.string.health_connect_not_installed))
            is State.ServiceNotRunning -> { /* unreachable code */ }
        }
    }

    private fun sendHealthData(context: Context, state: State.DataAvailable) {
        context.sendString(
            cn = pluginComponent,
            string = getUiString(state.data),
            private = true
        )
        context.sendCardProgress(
            cn = pluginComponent,
            progress = state.data.steps.toFloat() / STEPS_TARGET
        )
    }

    private fun genAndSendSearchResult(intent: Intent) {
        val data = intent.getStringExtra("data") ?: return
        val result = generateSearchResult(data) ?: return

        context.sendPluginResult(
            PluginResult(
                from = pluginComponent,
                data = result
            )
        )
    }

    private fun generateSearchResult(string: String): PluginResult? {
        val s = state.value as State.DataAvailable? ?: return null

        if (
            string.lowercase() == "health" ||
            string.lowercase() == context.getString(R.string.health).lowercase()
        ) {
            val line = PluginLine(
                body = getUiString(s.data),
                id = 0
            )

            return PluginResult(
                from = pluginComponent,
                data = SearchPluginLines(listOf(line))
            )
        }

        return null
    }

    private fun getUiString(data: HealthDataModel) =
        " %%fa:person-walking%% $NBSP" + data.stepsFormatted + ENSP.repeat(3) +
                " %%fa:road%% $NBSP" + data.distanceFormatted + ENSP.repeat(3) +
                " %%fa:heart%% $NBSP" + data.heartRateFormatted + ENSP.repeat(3) +
                " %%fa:fire%% $NBSP" + data.caloriesFormatted
}