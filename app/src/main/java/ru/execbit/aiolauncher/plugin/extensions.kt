package ru.execbit.aiolauncher.plugin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import ru.execbit.aiohealthplugin.AIO_API_VERSION
import ru.execbit.aiolauncher.models.PluginError
import ru.execbit.aiolauncher.models.PluginIntentActions
import ru.execbit.aiolauncher.models.PluginResult
import ru.execbit.aiohealthplugin.App
import ru.execbit.aiohealthplugin.Settings
import ru.execbit.aiolauncher.models.PluginCardProgress
import ru.execbit.aiolauncher.models.PluginLine
import ru.execbit.aiolauncher.models.PluginLines

fun Context.sendPluginResult(result: PluginResult) {
    val i = Intent(PluginIntentActions.AIO_UPDATE).apply {
        `package` = "ru.execbit.aiolauncher"
        putExtra("api", AIO_API_VERSION)
        putExtra("result", result)
        putExtra("uid", Settings.pluginUid)
    }

    sendBroadcast(i)
}

fun Context.sendInvalidAioVersionError(cn: ComponentName) {
    sendPluginResult(
        PluginResult(
            from = cn,
            data = PluginError(5, "Update AIO Launcher")
        )
    )
}

fun Context.sendString(
    cn: ComponentName,
    string: String,
    private: Boolean = false
) {
    val line = PluginLine(
        body = string,
        id = 0
    )

    sendPluginResult(
        PluginResult(
            from = cn,
            data = PluginLines(
                lines = listOf(line),
                privateModeSupport = private,
                foldable = false,
            )
        )
    )
}

fun Context.sendCardProgress(
    cn: ComponentName,
    progress: Float,
) {
    sendPluginResult(
        PluginResult(
            from = cn,
            data = PluginCardProgress(progress)
        )
    )
}
