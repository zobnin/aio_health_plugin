package ru.execbit.aiohealthplugin

import android.content.ComponentName

const val REQUIRED_AIO_VERSION = "2.7.30"
const val AIO_API_VERSION = 2

const val NOTIFICATION_ID = 1
const val NOTIFICATION_CHANNEL_ID = "main"
const val INTENT_ACTION = "intent_action"

const val STEPS_TARGET = 10_000
const val MAX_HEART_RATE = 150

const val INTERNAL_ERROR = 2
const val INVALID_ACTION_ERROR = 4

val pluginComponent: ComponentName by lazy {
    ComponentName(App.context.packageName, PluginReceiver::class.qualifiedName!!)
}

