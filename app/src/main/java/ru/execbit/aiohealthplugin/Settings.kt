package ru.execbit.aiohealthplugin

import ru.execbit.preferences.Preference

object Settings {
    var lastPluginUpdateCheck by Preference(0L, "last_update_check")
    var notifyShowedForVersion by Preference(0, "notify_showed")
    var pluginUid by Preference("", "plugin_uid")
    var metricSystem by Preference(true, "metric_system")
}