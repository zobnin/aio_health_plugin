package ru.execbit.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class Preference<T>(
    private val defaultValue: T,
    private val key: String,
) {
    companion object {
        private lateinit var preferences: SharedPreferences

        fun init(context: Context) {
            preferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return when (defaultValue) {
            is Long -> preferences.getLong(key, defaultValue) as T
            is Int -> preferences.getInt(key, defaultValue) as T
            is Boolean -> preferences.getBoolean(key, defaultValue) as T
            is Float -> preferences.getFloat(key, defaultValue) as T
            is String -> preferences.getString(key, defaultValue) as T
            else -> throw IllegalArgumentException("Unsupported type.")
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        with(preferences.edit()) {
            when (value) {
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is String -> putString(key, value)
                else -> throw IllegalArgumentException("Unsupported type.")
            }
            apply()
        }
    }
}
