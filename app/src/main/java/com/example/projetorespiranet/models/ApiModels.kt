package com.example.projetorespiranet.models

import com.google.gson.annotations.SerializedName

// /api/nodes item
data class Node(
    val id: Int,
    val mac: String,
    val description: String,
    @SerializedName("last_seen") val lastSeen: String,
    val status: String
)

// /api/nodes/{id}/status response
// Sensors may be null per documentation
data class NodeStatus(
    val id: Int,
    val mac: String,
    val description: String,
    @SerializedName("last_seen") val lastSeen: String,
    val status: String,
    @SerializedName("air_quality") val airQuality: String,
    val sensors: Sensors
)

data class Sensors(
    val temperature: SensorReading?,
    val humidity: SensorReading?,
    val luminosity: SensorReading?,
    val gas: GasSensorReading?
)

data class SensorReading(
    val value: Double,
    val ts: String
)

data class GasSensorReading(
    val value: Double,
    val ts: String,
    val type: String?
)

// /api/nodes/status/summary
 data class StatusSummary(
    @SerializedName("total_nodes") val totalNodes: Int,
    @SerializedName("online_nodes") val onlineNodes: Int,
    @SerializedName("offline_nodes") val offlineNodes: Int,
    val locations: List<LocationStatus>
 )

 data class LocationStatus(
    val location: String,
    val total: Int,
    val online: Int,
    val offline: Int
 )

// historical readings
 data class Reading(
    @SerializedName("node_id") val nodeId: Int,
    val value: Double,
    val ts: String
 )

 data class GasReading(
    @SerializedName("node_id") val nodeId: Int,
    val value: Double,
    val ts: String,
    val type: String
 )

// latest readings
 data class LatestReadings(
    val nodeId: Int,
    val mac: String,
    val timestamp: String,
    val readings: LatestReadingsData
 )

 data class LatestReadingsData(
    val temperature: SensorReading?,
    val humidity: SensorReading?,
    val luminosity: SensorReading?,
    val gas: List<GasSensorReading>
 )
