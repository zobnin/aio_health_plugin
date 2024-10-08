package ru.execbit.aiohealthplugin

import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

class HealthData(private val healthWrapper: HealthConnectWrapper) {

    val isHealthConnectAvailable: Boolean
        get() = healthWrapper.isAvailable()

    suspend fun checkPermissions(): Boolean {
        return healthWrapper.checkPermissions()
    }

    fun connect() {
        healthWrapper.connect()
    }

    suspend fun getHealthData(): HealthDataModel {
        val aggregateDataTypes = setOf(
            StepsRecord.COUNT_TOTAL,
            DistanceRecord.DISTANCE_TOTAL,
            TotalCaloriesBurnedRecord.ENERGY_TOTAL,
        )

        val timeRangeFilter = TimeRangeFilter.between(
            LocalDate.now().atStartOfDay(),
            LocalDateTime.now()
        )

        val aggregateRequest = AggregateRequest(
            metrics = aggregateDataTypes,
            timeRangeFilter = timeRangeFilter,
            dataOriginFilter = setOf()
        )

        val aggrData = healthWrapper.aggregate(aggregateRequest)

        val distanceKm = aggrData[DistanceRecord.DISTANCE_TOTAL]?.inKilometers ?: 0.0
        val distanceMiles = aggrData[DistanceRecord.DISTANCE_TOTAL]?.inMiles ?: 0.0
        val totalCalories = aggrData[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
        val stepsNum = aggrData[StepsRecord.COUNT_TOTAL] ?: 0L

        val heartRateData = healthWrapper.readData<HeartRateRecord>(timeRangeFilter)
        val lastHeartRate = heartRateData.lastOrNull()?.samples?.lastOrNull()?.beatsPerMinute ?: 0L

        return HealthDataModel(
            timestamp = Date().time,
            steps = stepsNum,
            heartRate = lastHeartRate,
            distanceKm = distanceKm,
            distanceMiles = distanceMiles,
            kCalories = totalCalories,
        )
    }
}