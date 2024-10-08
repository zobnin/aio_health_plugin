package ru.execbit.aiohealthplugin

import android.annotation.SuppressLint
import android.graphics.Color
import java.text.DecimalFormat
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
data class HealthDataModel(
    val timestamp: Long,
    val steps: Long,
    val heartRate: Long,
    val distanceKm: Double,
    val distanceMiles: Double,
    val kCalories: Double,
) {
    val distanceFormatted: String
        get() = if (Settings.metricSystem) {
            DecimalFormat("0.00").format(distanceKm) + " km"
        } else {
            DecimalFormat("0.00").format(distanceMiles) + " mi"
        }

    val caloriesFormatted: String
        get() = String.format("%,d", kCalories.roundToInt()) + " kCal"

    val heartRateFormatted: CharSequence
        get() = if (heartRate > MAX_HEART_RATE) {
            heartRate.toString().color(Color.RED)
        } else {
            heartRate.toString()
        }

    val stepsFormatted: CharSequence
        get() = if (steps > STEPS_TARGET) {
            String.format("%,d", steps).accentColor()
        } else {
            String.format("%,d", steps)
        }

    private fun String.color(color: Int): String {
        return "<font color=\"" + color.toHexColor() + "\">" + this + "</font>"
    }

    private fun String.accentColor(): String {
        return if (SettingsActivity.accentColor != 0) {
            this.color(SettingsActivity.accentColor)
        } else {
            this
        }
    }

    private fun Int.toHexColor(): String {
        return String.format("#%06X", 0xFFFFFF and this)
    }
}
