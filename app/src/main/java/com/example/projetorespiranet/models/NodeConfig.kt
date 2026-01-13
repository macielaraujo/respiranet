package com.example.projetorespiranet.models

data class NodeConfig(
    val mac: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class AppConfig(
    val baseUrl: String,
    val nodes: List<NodeConfig>
)
