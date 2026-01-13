package com.example.projetorespiranet.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.projetorespiranet.models.AppConfig
import com.example.projetorespiranet.models.NodeConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ConfigManager {
    private const val PREFS_NAME = "respira_prefs"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_NODES_CONFIG = "nodes_config"
//    private const val DEFAULT_BASE_URL = "http://192.168.1.100:8000/"

    private const val DEFAULT_BASE_URL = "http://192.168.0.6:8000/"  //servidor máquina mauro
//private const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/" //servidor para rodar no emulador
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveBaseUrl(context: Context, baseUrl: String) {
        getPrefs(context).edit().putString(KEY_BASE_URL, baseUrl).apply()
    }

    fun getBaseUrl(context: Context): String {
        return getPrefs(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    fun saveNodesConfig(context: Context, nodes: List<NodeConfig>) {
        val gson = Gson()
        val json = gson.toJson(nodes)
        getPrefs(context).edit().putString(KEY_NODES_CONFIG, json).apply()
    }

    fun getNodesConfig(context: Context): List<NodeConfig> {
        val json = getPrefs(context).getString(KEY_NODES_CONFIG, null)
        if (json.isNullOrEmpty()) {
            return getDefaultNodesConfig()
        }
        return try {
            val gson = Gson()
            val type = object : TypeToken<List<NodeConfig>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            getDefaultNodesConfig()
        }
    }

    fun getNodeByMac(context: Context, mac: String): NodeConfig? {
        return getNodesConfig(context).find { it.mac.equals(mac, ignoreCase = true) }
    }

    fun addOrUpdateNode(context: Context, node: NodeConfig) {
        val nodes = getNodesConfig(context).toMutableList()
        val index = nodes.indexOfFirst { it.mac.equals(node.mac, ignoreCase = true) }
        if (index != -1) nodes[index] = node else nodes.add(node)
        saveNodesConfig(context, nodes)
    }

    fun removeNode(context: Context, mac: String) {
        val nodes = getNodesConfig(context).filter { !it.mac.equals(mac, ignoreCase = true) }
        saveNodesConfig(context, nodes)
    }

    private fun getDefaultNodesConfig(): List<NodeConfig> {
        return listOf(
            NodeConfig(
                mac = "AA:BB:CC:DD:EE:FF",
                name = "Restaurante Universitário",
                latitude = -3.693600,
                longitude = -40.355156
            ),
            NodeConfig(
                mac = "AA:BB:CC:DD:EE:01",
                name = "Bloco 1: Merendeiro",
                latitude = -3.693269,
                longitude = -40.354094
            ),
            NodeConfig(
                mac = "AA:BB:CC:DD:EE:02",
                name = "Bloco 2: Odontologia",
                latitude = -3.694030,
                longitude = -40.354559
            )
        )
    }

    fun resetToDefaults(context: Context) {
        saveBaseUrl(context, DEFAULT_BASE_URL)
        saveNodesConfig(context, getDefaultNodesConfig())
    }

    fun getAppConfig(context: Context): AppConfig {
        return AppConfig(
            baseUrl = getBaseUrl(context),
            nodes = getNodesConfig(context)
        )
    }
}
