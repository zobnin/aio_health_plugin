package ru.execbit.aiohealthplugin

import android.os.Bundle
import android.preference.PreferenceFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class SettingsFragment : PreferenceFragment() {
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onResume() {
        super.onResume()
        stateSubscribe()
    }

    override fun onPause() {
        super.onPause()
        stateUnsubscribe()
    }

    private fun stateSubscribe() {
        scope.launch {
            state.collect { newState ->
                when (newState) {
                    is State.NotAvailable -> showNotInstalledStatus()
                    is State.NoPermission -> showNoPermissionStatus()
                    is State.ServiceNotRunning -> showServiceNotWorkingStatus()
                    else -> showWorkingStatus()
                }
            }
        }
    }

    private fun stateUnsubscribe() {
        scope.coroutineContext.cancelChildren()
    }

    private fun showNotInstalledStatus() {
        findPreference("status")?.apply {
            summary = getString(R.string.health_connect_not_installed)
            setOnPreferenceClickListener {
                context.openAppStoreHealth()
                true
            }
        }
    }

    private fun showNoPermissionStatus() {
        findPreference("status")?.apply {
            summary = getString(R.string.error_no_permission)
            setOnPreferenceClickListener {
                (activity as SettingsActivity).requestHealthPermissions()
                true
            }
        }
    }

    private fun showServiceNotWorkingStatus() {
        findPreference("status").summary = getString(R.string.error_service_not_working)
    }

    private fun showWorkingStatus() {
        findPreference("status").summary = getString(R.string.working)
    }
}
