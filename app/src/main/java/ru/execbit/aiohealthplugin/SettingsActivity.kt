package ru.execbit.aiohealthplugin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.health.connect.client.PermissionController
import com.google.android.material.color.MaterialColors
import ru.execbit.aiolauncher.models.PluginIntentActions

@Suppress("DEPRECATION")
@SuppressLint("ExportedPreferenceActivity")
class SettingsActivity : ComponentActivity() {
    companion object {
        var accentColor = 0
            private set
    }

    private val permissionRequest = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) {
        // Service will check permissions
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadFragment()
        requestHealthPermissions()
        updateAccentColor()
    }

    override fun onResume() {
        super.onResume()
        val loadIntent = Intent(PluginIntentActions.PLUGIN_GET_DATA).apply {
            putExtra("event", "load")
        }
        PluginService.startService(this, loadIntent)
    }

    private fun updateAccentColor() {
        accentColor = MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorAccent,
            0
        )
    }

    private fun loadFragment() {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    fun requestHealthPermissions() {
        permissionRequest.launch(HealthConnectWrapper.permissions)
    }
}