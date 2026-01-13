package com.example.projetorespiranet.network

import com.example.projetorespiranet.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RespiraNetApi {
    @GET("api/nodes")
    suspend fun getNodes(): List<Node>

    @GET("api/nodes/{id}")
    suspend fun getNode(@Path("id") nodeId: Int): Node

    @GET("api/nodes/{id}/status")
    suspend fun getNodeStatus(@Path("id") nodeId: Int): NodeStatus

    @GET("api/nodes/status/summary")
    suspend fun getStatusSummary(): StatusSummary

    @GET("api/readings/{nodeId}/latest")
    suspend fun getLatestReadings(@Path("nodeId") nodeId: Int): LatestReadings

    @GET("api/readings/temperature")
    suspend fun getTemperatureReadings(
        @Query("nodeId") nodeId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<Reading>

    @GET("api/readings/humidity")
    suspend fun getHumidityReadings(
        @Query("nodeId") nodeId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<Reading>

    @GET("api/readings/luminosity")
    suspend fun getLuminosityReadings(
        @Query("nodeId") nodeId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<Reading>

    @GET("api/readings/gas")
    suspend fun getGasReadings(
        @Query("nodeId") nodeId: Int,
        @Query("gasType") gasType: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<GasReading>
}
