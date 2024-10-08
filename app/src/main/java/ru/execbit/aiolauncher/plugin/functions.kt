package ru.execbit.aiolauncher.plugin

import android.content.Context
import android.content.Intent
import ru.execbit.aiohealthplugin.Settings
import java.util.*
import java.util.regex.Pattern

fun checkAioVersion(context: Context, requiredVersion: String): Boolean {
    // We use 1000.0.0 for cases when plugin can't determine AIO Version
    val actualVersion = context.packageManager
        .getPackageInfo("ru.execbit.aiolauncher", 0)?.versionName ?: "1000.0.0"

    return try {
        isFirmwareNewer(actualVersion, requiredVersion)
    } catch (e: Exception) {
        e.printStackTrace()
        // We don't want to make plugin unusable in situations
        // when developer make mistake in version number
        true
    }
}

private fun getVersionNumbers(ver: String?): IntArray {
    val m = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(-beta(\\d*))?").matcher(ver)
    require(m.matches()) { "Malformed FW version" }
    return intArrayOf(
        m.group(1).toInt(), m.group(2).toInt(), m.group(3).toInt(),  // rev.
        if (m.group(4) == null) Int.MAX_VALUE else if (m.group(5).isEmpty()) 1 // "beta"
        else m.group(5).toInt() // "beta3"
    )
}

private fun isFirmwareNewer(testFW: String?, baseFW: String?): Boolean {
    val testVer = getVersionNumbers(testFW)
    val baseVer = getVersionNumbers(baseFW)
    for (i in testVer.indices) if (testVer[i] != baseVer[i]) return testVer[i] > baseVer[i]
    return true
}

fun checkUid(intent: Intent): Boolean {
    val uid = intent.getStringExtra("uid") ?: ""

    if (uid.isEmpty()) {
        return false
    }

    if (Settings.pluginUid.isEmpty()) {
        Settings.pluginUid = uid
        return true
    } else {
        if (Settings.pluginUid == uid) {
            return true
        }
    }

    return false
}
