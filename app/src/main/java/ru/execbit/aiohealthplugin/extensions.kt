package ru.execbit.aiohealthplugin

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import ru.execbit.aiolauncher.models.PluginActivity
import ru.execbit.aiolauncher.models.PluginResult
import ru.execbit.aiolauncher.plugin.sendPluginResult

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

fun Context.openFitnessApp(cn: ComponentName?) {
    val result = PluginResult(
        from = cn,
        data = PluginActivity(
            action = Intent.ACTION_MAIN,
            component = ComponentName(
                "com.google.android.apps.fitness",
                "com.google.android.apps.fitness.shared.container.MainActivity"
            )
        )
    )
    sendPluginResult(result)
}

fun Context.openMainActivity(cn: ComponentName?) {
    val result = PluginResult(
        from = cn,
        data = PluginActivity(
            action = Intent.ACTION_MAIN,
            component = ComponentName(this, SettingsActivity::class.java)
        )
    )
    sendPluginResult(result)
}

fun Context.openAppStore(cn: ComponentName?, id: String) {
    openUrl(cn, "https://play.google.com/store/apps/details?id=$id")
}

fun Context.openAppStoreHealth() {
    openAppStore(pluginComponent, "com.google.android.apps.healthdata")
}

fun Context.openUrl(cn: ComponentName?, uri: String) {
    val result = PluginResult(
        from = cn,
        data = PluginActivity(
            action = Intent.ACTION_VIEW,
            data = Uri.parse(uri)
        )
    )
    sendPluginResult(result)
}

inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(name: String): T? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(name, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(name) as? T
    }
}
