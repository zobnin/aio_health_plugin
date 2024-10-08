package ru.execbit.aiohealthplugin

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter

class HealthConnectWrapper {
    companion object {
        val permissions =
            setOf(
                HealthPermission.getReadPermission(HeartRateRecord::class),
                HealthPermission.getReadPermission(StepsRecord::class),
                HealthPermission.getReadPermission(DistanceRecord::class),
                HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            )
    }

    var healthConnectClient: HealthConnectClient? = null
        private set

    fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(App.context) ==
                HealthConnectClient.SDK_AVAILABLE
    }

    fun connect() {
        if (healthConnectClient != null) return

        healthConnectClient = HealthConnectClient.getOrCreate(App.context)
    }

    suspend fun checkPermissions(): Boolean {
        check(healthConnectClient != null)

        return healthConnectClient!!
            .permissionController
            .getGrantedPermissions()
            .containsAll(permissions)
    }

    suspend inline fun <reified T : Record> readData(
        timeRangeFilter: TimeRangeFilter,
        dataOriginFilter: Set<DataOrigin> = setOf()
    ): List<T> {
        check(healthConnectClient != null)

        val request = ReadRecordsRequest(
            recordType = T::class,
            dataOriginFilter = dataOriginFilter,
            timeRangeFilter = timeRangeFilter
        )
        return healthConnectClient!!.readRecords(request).records
    }

    suspend fun aggregate(request: AggregateRequest): AggregationResult {
        check(healthConnectClient != null)

        return healthConnectClient!!.aggregate(request)
    }
}