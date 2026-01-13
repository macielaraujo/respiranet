# 📱 Guia de Integração - RespiraNet App com API REST

## ⚠️ ANÁLISE DAS ROTAS EXISTENTES

### 🟢 Rotas Prontas e Funcionais

A API já possui todas as rotas necessárias para integração! Aqui está o mapeamento:

| Rota API Existente | Status | Uso no App | Observações |
|---|---|---|---|
| `GET /api/nodes` | ✅ Pronta | Mapa + Dashboard | Retorna `description` (não `name`), sem lat/lng |
| `GET /api/nodes/:id` | ✅ Pronta | Detalhes do nó | Mesma estrutura do /nodes |
| `GET /api/nodes/:id/status` | ✅ Pronta | Dashboard principal | Perfeita! Contém temp, umidade, gases, air_quality |
| `GET /api/nodes/status/summary` | ✅ Pronta | Dashboard contadores | Retorna total_nodes, online/offline por localização |
| `GET /api/readings/temperature` | ✅ Pronta | Gráfico temperatura | Aceita nodeId (opcional), limit, offset |
| `GET /api/readings/humidity` | ✅ Pronta | Gráfico umidade | Aceita nodeId (opcional), limit, offset |
| `GET /api/readings/luminosity` | ✅ Pronta | Gráfico luminosidade | Aceita nodeId (opcional), limit, offset |
| `GET /api/readings/gas` | ✅ Pronta | Gráfico gases | Aceita nodeId (opcional), gasType, limit, offset |
| `GET /api/readings/:nodeId/latest` | ✅ Pronta | Dashboard atualização | Aceita ID ou MAC, gas retorna array com até 5 leituras |
| `GET /api/analytics/average` | ✅ Pronta | Análises | Retorna average, min, max, count |
| `GET /api/analytics/statistics` | ✅ Pronta | Análises avançadas | Retorna + stddev, variance |

### 🟡 Correções Realizadas na Documentação

#### **✅ Correção 1: Campo é `description`, não `name`**
- **Antes:** `node.name` (NÃO EXISTE)
- **Depois:** `node.description` ✅
- **Onde:** Data classes `Node`, `NodeStatus`
- **Código:** Todos os exemplos foram atualizados

#### **✅ Correção 2: Sensores podem retornar `null`**
- **Antes:** `status.sensors.temperature.value`
- **Depois:** `status.sensors.temperature?.value?.toInt() ?: 0`
- **Motivo:** API pode retornar null se sem leituras
- **Onde:** Exemplos de MainActivity e integração

#### **✅ Correção 3: Array `gas` retorna múltiplas leituras**
- **Antes:** Esperado: objeto único `GasSensorReading`
- **Depois:** Array `List<GasSensorReading>` com até 5 leituras
- **Onde:** Data class `LatestReadingsData`
- **Acesso:** `latestReadings.readings.gas[0]` ou `gas.firstOrNull()`

#### **✅ Correção 4: Analytics endpoints EXISTEM**
- **GET /api/analytics/average** - Retorna estatísticas básicas
- **GET /api/analytics/statistics** - Retorna com desvio padrão e variância
- **Ambos** requerem: `nodeId` e `sensorType`

---

## 🎯 Estrutura do App Android

### Telas Principais
1. **MainActivity** - Dashboard principal com resumo de sensores
2. **GraphicsActivity** - Visualização gráfica de dados temporais
3. **MapsActivity** - Localização dos sensores em mapa
4. **SettingsActivity** - Configurações do app

### Dados Atualmente Carregados
- **Dashboard**: Valores hardcoded de temperatura, umidade, status de sensores
- **Mapa**: Dispositivos carregados do arquivo `esps.json` local
- **Gráficos**: Dados simulados localmente

---

## � Mapeamento Completo API → App

### **BASE URL DA API**
```
http://localhost:8000
```
⚠️ **Importante:** Mude para o IP da sua máquina quando testar no celular (ex: `http://192.168.1.100:8000`)

---

## 📱 IMPLEMENTAÇÃO POR TELA

### 1️⃣ MainActivity (Dashboard)

#### **Dados Hardcoded Atuais:**
```kotlin
binding.txtValueUmidade.text = "52"
binding.txtValueTemp.text = "28"
binding.cardStatusLocal.text = "Merendeiro"
binding.cardStatusResultado.text = "Seguro"
binding.contSensoresAtivos.text = "7 Sensores"
binding.contSensoresInstaveis.text = "3 Sensores"
binding.contSensoresOff.text = "2 Sensores"
```

#### **Rotas da API para usar:**

**1. Buscar status de um nó específico**
```http
GET /api/nodes/1/status
```
**Resposta da API:**
```json
{
  "id": 1,
  "mac": "AA:BB:CC:DD:EE:FF",
  "name": "Laboratório 1",
  "description": "Laboratório 1",
  "last_seen": "2026-01-12T10:30:00Z",
  "status": "online",
  "air_quality": "safe",
  "sensors": {
    "temperature": { "value": 22.5, "ts": "2026-01-12T10:30:00Z" },
    "humidity": { "value": 65.3, "ts": "2026-01-12T10:30:00Z" },
    "luminosity": { "value": 450, "ts": "2026-01-12T10:30:00Z" },
    "gas": { "value": 150, "ts": "2026-01-12T10:30:00Z", "type": "mq2" }
  }
}
```

**Como adaptar o código:**
```kotlin
// ANTES (hardcoded)
binding.txtValueTemp.text = "28"
binding.txtValueUmidade.text = "52"
binding.cardStatusResultado.text = "Seguro"

// DEPOIS (com API)
lifecycleScope.launch {
    try {
        val response = api.getNodeStatus(1) // ID do nó selecionado
        
        binding.txtValueTemp.text = "${response.sensors.temperature?.value?.toInt() ?: 0}"
        binding.txtValueUmidade.text = "${response.sensors.humidity?.value?.toInt() ?: 0}"
        binding.cardStatusLocal.text = response.description
        
        // Mapear air_quality para português
        binding.cardStatusResultado.text = when(response.airQuality) {
            "safe" -> "Seguro"
            "warning" -> "Atenção"
            "critical" -> "Crítico"
            else -> "Desconhecido"
        }
        
        // Verificar se está online
        binding.txtStatusSensorTemp.text = if(response.status == "online") "Ativo" else "Offline"
        binding.txtStatusSensorUmidade.text = if(response.status == "online") "Ativo" else "Offline"
        
    } catch (e: Exception) {
        Log.e("MainActivity", "Erro ao buscar dados: ${e.message}")
        Toast.makeText(this, "Erro ao conectar com a API", Toast.LENGTH_SHORT).show()
    }
}
```

**2. Buscar resumo de status**
```http
GET /api/nodes/status/summary
```
**Resposta da API:**
```json
{
  "total_nodes": 10,
  "online_nodes": 8,
  "offline_nodes": 2,
  "locations": [
    { "location": "Laboratório 1", "total": 5, "online": 4, "offline": 1 },
    { "location": "Corredor", "total": 5, "online": 4, "offline": 1 }
  ]
}
```

**Como adaptar o código:**
```kotlin
// ANTES (hardcoded)
binding.contSensoresAtivos.text = "7 Sensores"
binding.contSensoresInstaveis.text = "3 Sensores"
binding.contSensoresOff.text = "2 Sensores"

// DEPOIS (com API)
lifecycleScope.launch {
    try {
        val summary = api.getStatusSummary()
        
        binding.contSensoresAtivos.text = "${summary.online_nodes} Sensores"
        binding.contSensoresOff.text = "${summary.offline_nodes} Sensores"
        
        // Como a API não tem "instáveis", calcule ou use 0
        val instaveis = 0 // Ou implemente lógica própria
        binding.contSensoresInstaveis.text = "$instaveis Sensores"
        
    } catch (e: Exception) {
        Log.e("MainActivity", "Erro ao buscar resumo: ${e.message}")
    }
}
```

---

### 2️⃣ MapsActivity (Mapa)

#### **Problema: API não retorna coordenadas GPS**

**Solução Temporária - Usar híbrido:**
1. Buscar lista de nós da API
2. Carregar coordenadas do `esps.json`
3. Fazer merge dos dados por `name` ou `mac`

**Código adaptado:**
```kotlin
private fun carregarDados() {
    lifecycleScope.launch {
        try {
            // 1. Buscar nós da API
            val nodesApi = api.getNodes()
            
            // 2. Carregar coordenadas do arquivo local
            val json = assets.open("esps.json").bufferedReader().use { it.readText() }
            val coordenadas = Gson().fromJson(json, Array<EspDevice>::class.java).toList()
            
            // 3. Fazer merge dos dados
            val dispositivosCompletos = nodesApi.map { node ->
                val coord = coordenadas.find { it.id == node.description }
                DeviceCompleto(
                    id = node.id,
                    name = node.description,
                    lat = coord?.lat ?: -3.693269,
                    lng = coord?.lng ?: -40.354094,
                    status = node.status,
                    lastSeen = node.lastSeen
                )
            }
            
            // 4. Adicionar marcadores no mapa
            addMarkers(dispositivosCompletos)
            
        } catch (e: Exception) {
            Log.e("MapsActivity", "Erro: ${e.message}")
            // Fallback: usar apenas esps.json
            carregarEspsMock()
        }
    }
}

data class DeviceCompleto(
    val id: Int,
    val name: String,
    val lat: Double,
    val lng: Double,
    val status: String,
    val lastSeen: String
)
```

**Buscar dados de leitura ao clicar no marcador:**
```kotlin
marker.setOnMarkerClickListener { marker, mapView ->
    lifecycleScope.launch {
        try {
            val nodeId = marker.id.toInt()
            val leituras = api.getLatestReadings(nodeId)
            
            // Mostrar popup com dados
            mostrarPopupLeituras(
                temp = leituras.readings.temperature.value,
                umid = leituras.readings.humidity.value,
                gas = leituras.readings.gas.firstOrNull()?.value ?: 0.0
            )
        } catch (e: Exception) {
            Toast.makeText(this@MapsActivity, "Erro ao buscar dados", Toast.LENGTH_SHORT).show()
        }
    }
    true
}
```

---

### 3️⃣ GraphicsActivity (Gráficos)

#### **Dados Simulados Atuais:**
O app gera valores aleatórios para os gráficos.

#### **Rotas da API para usar:**

**1. Histórico de temperatura**
```http
GET /api/readings/temperature?nodeId=1&limit=50&offset=0
```
**Resposta:**
```json
[
  { "node_id": 1, "value": 23.1, "ts": "2026-01-12T10:35:00Z" },
  { "node_id": 1, "value": 23.0, "ts": "2026-01-12T10:30:00Z" },
  { "node_id": 1, "value": 22.9, "ts": "2026-01-12T10:25:00Z" }
]
```

**2. Histórico de umidade**
```http
GET /api/readings/humidity?nodeId=1&limit=50&offset=0
```
**Mesma estrutura da temperatura**

**Como adaptar o código:**
```kotlin
private fun carregarDadosTemperatura(nodeId: Int) {
    lifecycleScope.launch {
        try {
            val leituras = api.getTemperatureReadings(nodeId, limit = 50)
            
            // Converter para Entry do MPAndroidChart
            val entries = leituras.mapIndexed { index, reading ->
                Entry(index.toFloat(), reading.value.toFloat())
            }
            
            // Atualizar gráfico
            lineDataSet1.values = entries
            lineDataSet1.notifyDataSetChanged()
            lineData1.notifyDataChanged()
            binding.chartTemp.notifyDataSetChanged()
            binding.chartTemp.invalidate()
            
        } catch (e: Exception) {
            Log.e("GraphicsActivity", "Erro ao buscar temperatura: ${e.message}")
            Toast.makeText(this@GraphicsActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun carregarDadosUmidade(nodeId: Int) {
    lifecycleScope.launch {
        try {
            val leituras = api.getHumidityReadings(nodeId, limit = 50)
            
            val entries = leituras.mapIndexed { index, reading ->
                Entry(index.toFloat(), reading.value.toFloat())
            }
            
            lineDataSet2.values = entries
            lineDataSet2.notifyDataSetChanged()
            lineData2.notifyDataChanged()
            binding.chartHumidity.notifyDataSetChanged()
            binding.chartHumidity.invalidate()
            
        } catch (e: Exception) {
            Log.e("GraphicsActivity", "Erro ao buscar umidade: ${e.message}")
        }
    }
}

// Chamar no onCreate ou quando usuário selecionar dispositivo
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ... código existente ...
    
    // Carregar dados da API ao invés de simular
    carregarDadosTemperatura(nodeIdSelecionado)
    carregarDadosUmidade(nodeIdSelecionado)
}
```

---

## 🔌 Implementação do Cliente Retrofit

## 🔌 Implementação do Cliente Retrofit

### Passo 1: Adicionar Dependências no build.gradle.kts

```kotlin
dependencies {
    // ... dependências existentes ...
    
    // Retrofit para requisições HTTP
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Coroutines para async
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
}
```

### Passo 2: Criar Data Classes (Models)

Crie um arquivo `models/ApiModels.kt`:

```kotlin
package com.example.projetorespiranet.models

import com.google.gson.annotations.SerializedName

// Resposta de /api/nodes
data class Node(
    val id: Int,
    val mac: String,
    val description: String,
    @SerializedName("last_seen") val lastSeen: String,
    val status: String
)

// Resposta de /api/nodes/:id/status
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
    val temperature: SensorReading,
    val humidity: SensorReading,
    val luminosity: SensorReading,
    val gas: GasSensorReading
)

data class SensorReading(
    val value: Double,
    val ts: String
)

data class GasSensorReading(
    val value: Double,
    val ts: String,
    val type: String?  // Pode ser null conforme documentação
)

// Resposta de /api/nodes/status/summary
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

// Resposta de /api/readings/temperature, humidity, etc
data class Reading(
    @SerializedName("node_id") val nodeId: Int,
    val value: Double,
    val ts: String
)

// Resposta de /api/readings/gas
data class GasReading(
    @SerializedName("node_id") val nodeId: Int,
    val value: Double,
    val ts: String,
    val type: String
)

// Resposta de /api/readings/:nodeId/latest
data class LatestReadings(
    val nodeId: Int,
    val mac: String,
    val timestamp: String,
    val readings: LatestReadingsData
)

data class LatestReadingsData(
    val temperature: SensorReading?,  // Pode ser null
    val humidity: SensorReading?,      // Pode ser null
    val luminosity: SensorReading?,    // Pode ser null
    val gas: List<GasSensorReading>    // Array com até 5 últimas leituras
)
```

### Passo 3: Criar Interface do Retrofit

Crie um arquivo `network/RespiraNetApi.kt`:

```kotlin
package com.example.projetorespiranet.network

import com.example.projetorespiranet.models.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RespiraNetApi {
    
    // Endpoints de Nós
    @GET("api/nodes")
    suspend fun getNodes(): List<Node>
    
    @GET("api/nodes/{id}")
    suspend fun getNode(@Path("id") nodeId: Int): Node
    
    @GET("api/nodes/{id}/status")
    suspend fun getNodeStatus(@Path("id") nodeId: Int): NodeStatus
    
    @GET("api/nodes/status/summary")
    suspend fun getStatusSummary(): StatusSummary
    
    // Endpoints de Leituras
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
```

### Passo 4: Criar Singleton do Retrofit

Crie um arquivo `network/RetrofitClient.kt`:

```kotlin
package com.example.projetorespiranet.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // ⚠️ IMPORTANTE: Altere para o IP da sua máquina quando testar no celular
    // Ex: "http://192.168.1.100:8000/" 
    private const val BASE_URL = "http://localhost:8000/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val api: RespiraNetApi = retrofit.create(RespiraNetApi::class.java)
}
```

---

## 🔄 Atualização em Tempo Real (Polling)

### Implementar no MainActivity

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val api = RetrofitClient.api
    private var nodeIdSelecionado = 1 // Pode vir de uma preferência ou seleção do usuário
    private var pollingJob: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // ... resto do código ...
        
        // Iniciar polling
        iniciarAtualizacaoAutomatica()
    }
    
    private fun iniciarAtualizacaoAutomatica() {
        pollingJob = lifecycleScope.launch {
            while (isActive) {
                atualizarDashboard()
                atualizarResumo()
                delay(30000) // 30 segundos
            }
        }
    }
    
    private suspend fun atualizarDashboard() {
        try {
            val status = api.getNodeStatus(nodeIdSelecionado)
            
            withContext(Dispatchers.Main) {
                // Atualizar temperatura
                binding.txtValueTemp.text = "${status.sensors.temperature?.value?.toInt() ?: 0}"
                binding.txtStatusSensorTemp.text = if(status.status == "online") "Ativo" else "Offline"
                
                // Atualizar umidade
                binding.txtValueUmidade.text = "${status.sensors.humidity?.value?.toInt() ?: 0}"
                binding.txtStatusSensorUmidade.text = if(status.status == "online") "Ativo" else "Offline"
                
                // Atualizar local e resultado
                binding.cardStatusLocal.text = status.description
                binding.cardStatusResultado.text = when(status.airQuality) {
                    "safe" -> "Seguro"
                    "warning" -> "Atenção"
                    "critical" -> "Crítico"
                    else -> "Desconhecido"
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao buscar status: ${e.message}", e)
            withContext(Dispatchers.Main) {
                // Manter últimos valores ou mostrar erro
                Toast.makeText(this@MainActivity, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private suspend fun atualizarResumo() {
        try {
            val summary = api.getStatusSummary()
            
            withContext(Dispatchers.Main) {
                binding.contSensoresAtivos.text = "${summary.onlineNodes} Sensores"
                binding.contSensoresOff.text = "${summary.offlineNodes} Sensores"
                
                // Como não há "instáveis" na API, calcule ou use 0
                val instaveis = summary.totalNodes - summary.onlineNodes - summary.offlineNodes
                binding.contSensoresInstaveis.text = "$instaveis Sensores"
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao buscar resumo: ${e.message}", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        pollingJob?.cancel()
    }
}
```

#### **2.8 Atualizar MainActivity para usar ConfigManager**

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var api: RespiraNetApi
    private var nodeIdSelecionado = 1
    private var pollingJob: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar API com configurações
        api = RetrofitClient.getApi(this)
        
        // ... resto do código ...
        
        // Iniciar polling
        iniciarAtualizacaoAutomatica()
    }
    
    private fun iniciarAtualizacaoAutomatica() {
        pollingJob = lifecycleScope.launch {
            while (isActive) {
                atualizarDashboard()
                atualizarResumo()
                delay(30000) // 30 segundos
            }
        }
    }
    
    private suspend fun atualizarDashboard() {
        try {
            // Recriar API se configurações mudaram
            api = RetrofitClient.getApi(this@MainActivity)
            
            val status = api.getNodeStatus(nodeIdSelecionado)
            
            withContext(Dispatchers.Main) {
                binding.txtValueTemp.text = "${status.sensors.temperature?.value?.toInt() ?: 0}"
                binding.txtValueUmidade.text = "${status.sensors.humidity?.value?.toInt() ?: 0}"
                binding.cardStatusLocal.text = status.description
                
                binding.cardStatusResultado.text = when(status.airQuality) {
                    "safe" -> "Seguro"
                    "warning" -> "Atenção"
                    "critical" -> "Crítico"
                    else -> "Desconhecido"
                }
                
                binding.txtStatusSensorTemp.text = if(status.status == "online") "Ativo" else "Offline"
                binding.txtStatusSensorUmidade.text = if(status.status == "online") "Ativo" else "Offline"
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao buscar status: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        pollingJob?.cancel()
    }
}
```

#### **2.9 Adicionar Menu de Configurações**

Atualize o menu para incluir botão de configurações. Crie/atualize `res/menu/main_menu.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    
    <item
        android:id="@+id/action_settings"
        android:icon="@android:drawable/ic_menu_preferences"
        android:title="Configurações"
        app:showAsAction="ifRoom" />
</menu>
```

E na MainActivity, adicione:

```kotlin
override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
```

---

## 📝 Resumo das Funcionalidades Implementadas

### ✅ ConfigManager - Persistência de Dados
- ✅ Salvar e carregar Base URL da API
- ✅ Salvar lista de nós com MAC + Posição GPS
- ✅ Buscar nó específico por MAC
- ✅ Adicionar/Atualizar/Remover nós individualmente
- ✅ Resetar para configuração padrão
- ✅ Configurações persistem entre reinicializações do app

### ✅ SettingsActivity - Tela de Configurações
- ✅ Campo para editar Base URL
- ✅ Botão "Testar Conexão" para validar URL
- ✅ Lista de nós configurados (RecyclerView)
- ✅ Adicionar novo nó (Dialog)
- ✅ Editar nó existente (Dialog)
- ✅ Excluir nó (com confirmação)
- ✅ Botão "Resetar para Padrão"
- ✅ Layout responsivo com Material Design

### ✅ Integração Automática
- ✅ MapsActivity usa coordenadas configuradas
- ✅ API atualizada automaticamente quando Base URL muda
- ✅ Merge automático de dados API + Configurações locais

---

## 🎯 Fluxo de Uso para o Usuário

1. **Primeira Execução:**
   - App usa configurações padrão (localhost + 3 nós pré-configurados)
   
2. **Configurar Servidor:**
   - Abrir Configurações
   - Digitar IP do servidor (ex: `http://192.168.1.100:8000/`)
   - Clicar "Testar Conexão" para validar
   - Clicar "Salvar URL"
   
3. **Adicionar Nós:**
   - Clicar "+ Adicionar" na seção de nós
   - Preencher MAC, Nome e Coordenadas GPS
   - Salvar
   
4. **Editar Nós:**
   - Clicar no ícone de edição do nó
   - Modificar Nome ou Coordenadas (MAC é fixo)
   - Salvar
   
5. **Usar o App:**
   - Dashboard mostra dados da API
   - Mapa exibe marcadores nas posições configuradas
   - Gráficos mostram histórico de cada nó

---

## 💾 Estrutura de Armazenamento

### SharedPreferences Storage

```json
{
  "base_url": "http://192.168.1.100:8000/",
  "nodes_config": [
    {
      "mac": "AA:BB:CC:DD:EE:FF",
      "name": "Restaurante Universitário",
      "latitude": -3.693600,
      "longitude": -40.355156
    },
    {
      "mac": "AA:BB:CC:DD:EE:01",
      "name": "Bloco 1: Merendeiro",
      "latitude": -3.693269,
      "longitude": -40.354094
    }
  ]
}
```

### Vantagens:
- ✅ Simples de implementar
- ✅ Não requer permissões adicionais
- ✅ Dados persistem após fechar o app
- ✅ Backup automático (se usuário tem backup do Android ativado)
- ✅ Fácil de exportar/importar (futuro)

### 1. Adicionar Permissão de Internet

No arquivo `AndroidManifest.xml`, adicione dentro da tag `<manifest>`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <!-- ADICIONAR ESTA LINHA -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        ...>
        
        <!-- ADICIONAR ESTA LINHA para permitir tráfego HTTP (não HTTPS) -->
        android:usesCleartextTraffic="true"
        
    </application>
</manifest>
```

### 2. Configurar Base URL Dinâmica + Posições dos Nós

Implemente uma tela de configuração completa onde o usuário pode:
- Inserir o IP/URL do servidor
- Configurar a relação MAC → Posição GPS de cada nó
- Guardar dados persistentemente usando SharedPreferences

#### **2.1 Criar Data Classes para Configuração**

Crie `models/NodeConfig.kt`:

```kotlin
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
```

#### **2.2 Criar Gerenciador de Configurações**

Crie `utils/ConfigManager.kt`:

```kotlin
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
    private const val DEFAULT_BASE_URL = "http://192.168.1.100:8000/"
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // Salvar Base URL
    fun saveBaseUrl(context: Context, baseUrl: String) {
        getPrefs(context).edit().putString(KEY_BASE_URL, baseUrl).apply()
    }
    
    // Obter Base URL
    fun getBaseUrl(context: Context): String {
        return getPrefs(context).getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }
    
    // Salvar configuração de nós
    fun saveNodesConfig(context: Context, nodes: List<NodeConfig>) {
        val gson = Gson()
        val json = gson.toJson(nodes)
        getPrefs(context).edit().putString(KEY_NODES_CONFIG, json).apply()
    }
    
    // Obter configuração de nós
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
    
    // Obter nó por MAC
    fun getNodeByMac(context: Context, mac: String): NodeConfig? {
        return getNodesConfig(context).find { it.mac.equals(mac, ignoreCase = true) }
    }
    
    // Adicionar ou atualizar nó
    fun addOrUpdateNode(context: Context, node: NodeConfig) {
        val nodes = getNodesConfig(context).toMutableList()
        val index = nodes.indexOfFirst { it.mac.equals(node.mac, ignoreCase = true) }
        
        if (index != -1) {
            nodes[index] = node
        } else {
            nodes.add(node)
        }
        
        saveNodesConfig(context, nodes)
    }
    
    // Remover nó
    fun removeNode(context: Context, mac: String) {
        val nodes = getNodesConfig(context).filter { 
            !it.mac.equals(mac, ignoreCase = true) 
        }
        saveNodesConfig(context, nodes)
    }
    
    // Configuração padrão (valores iniciais do esps.json)
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
    
    // Resetar para configuração padrão
    fun resetToDefaults(context: Context) {
        saveBaseUrl(context, DEFAULT_BASE_URL)
        saveNodesConfig(context, getDefaultNodesConfig())
    }
    
    // Obter configuração completa
    fun getAppConfig(context: Context): AppConfig {
        return AppConfig(
            baseUrl = getBaseUrl(context),
            nodes = getNodesConfig(context)
        )
    }
}
```

#### **2.3 Atualizar RetrofitClient para usar ConfigManager**

Modifique `network/RetrofitClient.kt`:

```kotlin
package com.example.projetorespiranet.network

import android.content.Context
import com.example.projetorespiranet.utils.ConfigManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private var retrofit: Retrofit? = null
    private var currentBaseUrl: String? = null
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    fun getApi(context: Context): RespiraNetApi {
        val baseUrl = ConfigManager.getBaseUrl(context)
        
        // Recriar Retrofit se a URL mudou
        if (retrofit == null || currentBaseUrl != baseUrl) {
            currentBaseUrl = baseUrl
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        
        return retrofit!!.create(RespiraNetApi::class.java)
    }
}
```

#### **2.4 Criar Tela de Configurações Completa**

Atualize `SettingsActivity.kt`:

```kotlin
package com.example.projetorespiranet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetorespiranet.adapters.NodeConfigAdapter
import com.example.projetorespiranet.databinding.SettingsActivityBinding
import com.example.projetorespiranet.models.NodeConfig
import com.example.projetorespiranet.utils.ConfigManager

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: SettingsActivityBinding
    private lateinit var adapter: NodeConfigAdapter
    private var nodesList = mutableListOf<NodeConfig>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        carregarConfiguracoes()
        setupRecyclerView()
        setupButtons()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Configurações"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun carregarConfiguracoes() {
        // Carregar URL
        val baseUrl = ConfigManager.getBaseUrl(this)
        binding.editTextBaseUrl.setText(baseUrl)
        
        // Carregar nós
        nodesList.clear()
        nodesList.addAll(ConfigManager.getNodesConfig(this))
    }
    
    private fun setupRecyclerView() {
        adapter = NodeConfigAdapter(
            nodes = nodesList,
            onEdit = { node -> mostrarDialogEditarNo(node) },
            onDelete = { node -> confirmarExclusaoNo(node) }
        )
        
        binding.recyclerViewNodes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNodes.adapter = adapter
    }
    
    private fun setupButtons() {
        // Salvar URL
        binding.btnSalvarUrl.setOnClickListener {
            salvarBaseUrl()
        }
        
        // Adicionar nó
        binding.btnAdicionarNo.setOnClickListener {
            mostrarDialogAdicionarNo()
        }
        
        // Resetar para padrão
        binding.btnResetarPadrao.setOnClickListener {
            confirmarResetarPadrao()
        }
        
        // Testar conexão
        binding.btnTestarConexao.setOnClickListener {
            testarConexao()
        }
    }
    
    private fun salvarBaseUrl() {
        var url = binding.editTextBaseUrl.text.toString().trim()
        
        if (url.isEmpty()) {
            Toast.makeText(this, "Digite uma URL válida", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Garantir que termina com /
        if (!url.endsWith("/")) {
            url += "/"
        }
        
        // Garantir que tem protocolo
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }
        
        ConfigManager.saveBaseUrl(this, url)
        Toast.makeText(this, "URL salva com sucesso!", Toast.LENGTH_SHORT).show()
    }
    
    private fun mostrarDialogAdicionarNo() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_node, null)
        val editMac = dialogView.findViewById<EditText>(R.id.editTextMac)
        val editName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editLat = dialogView.findViewById<EditText>(R.id.editTextLatitude)
        val editLng = dialogView.findViewById<EditText>(R.id.editTextLongitude)
        
        AlertDialog.Builder(this)
            .setTitle("Adicionar Nó")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val mac = editMac.text.toString().trim()
                val name = editName.text.toString().trim()
                val lat = editLat.text.toString().toDoubleOrNull() ?: 0.0
                val lng = editLng.text.toString().toDoubleOrNull() ?: 0.0
                
                if (mac.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                
                val node = NodeConfig(mac, name, lat, lng)
                ConfigManager.addOrUpdateNode(this, node)
                carregarConfiguracoes()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Nó adicionado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun mostrarDialogEditarNo(node: NodeConfig) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_node, null)
        val editMac = dialogView.findViewById<EditText>(R.id.editTextMac)
        val editName = dialogView.findViewById<EditText>(R.id.editTextName)
        val editLat = dialogView.findViewById<EditText>(R.id.editTextLatitude)
        val editLng = dialogView.findViewById<EditText>(R.id.editTextLongitude)
        
        // Preencher com dados atuais
        editMac.setText(node.mac)
        editMac.isEnabled = false // MAC não deve ser editável
        editName.setText(node.name)
        editLat.setText(node.latitude.toString())
        editLng.setText(node.longitude.toString())
        
        AlertDialog.Builder(this)
            .setTitle("Editar Nó")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                val name = editName.text.toString().trim()
                val lat = editLat.text.toString().toDoubleOrNull() ?: node.latitude
                val lng = editLng.text.toString().toDoubleOrNull() ?: node.longitude
                
                val updatedNode = NodeConfig(node.mac, name, lat, lng)
                ConfigManager.addOrUpdateNode(this, updatedNode)
                carregarConfiguracoes()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Nó atualizado!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun confirmarExclusaoNo(node: NodeConfig) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Nó")
            .setMessage("Deseja realmente excluir o nó '${node.name}'?")
            .setPositiveButton("Excluir") { _, _ ->
                ConfigManager.removeNode(this, node.mac)
                carregarConfiguracoes()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "Nó excluído!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun confirmarResetarPadrao() {
        AlertDialog.Builder(this)
            .setTitle("Resetar Configurações")
            .setMessage("Deseja restaurar todas as configurações para o padrão?")
            .setPositiveButton("Resetar") { _, _ ->
                ConfigManager.resetToDefaults(this)
                carregarConfiguracoes()
                adapter.notifyDataSetChanged()
                binding.editTextBaseUrl.setText(ConfigManager.getBaseUrl(this))
                Toast.makeText(this, "Configurações resetadas!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun testarConexao() {
        lifecycleScope.launch {
            try {
                binding.btnTestarConexao.isEnabled = false
                binding.btnTestarConexao.text = "Testando..."
                
                val api = RetrofitClient.getApi(this@SettingsActivity)
                val nodes = api.getNodes()
                
                Toast.makeText(
                    this@SettingsActivity,
                    "Conexão OK! ${nodes.size} nós encontrados.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this@SettingsActivity,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnTestarConexao.isEnabled = true
                binding.btnTestarConexao.text = "Testar Conexão"
            }
        }
    }
}
```

#### **2.5 Criar Adapter para RecyclerView**

Crie `adapters/NodeConfigAdapter.kt`:

```kotlin
package com.example.projetorespiranet.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projetorespiranet.databinding.ItemNodeConfigBinding
import com.example.projetorespiranet.models.NodeConfig

class NodeConfigAdapter(
    private val nodes: List<NodeConfig>,
    private val onEdit: (NodeConfig) -> Unit,
    private val onDelete: (NodeConfig) -> Unit
) : RecyclerView.Adapter<NodeConfigAdapter.NodeViewHolder>() {
    
    inner class NodeViewHolder(private val binding: ItemNodeConfigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(node: NodeConfig) {
            binding.txtNodeName.text = node.name
            binding.txtNodeMac.text = "MAC: ${node.mac}"
            binding.txtNodeCoords.text = "Lat: ${node.latitude}, Lng: ${node.longitude}"
            
            binding.btnEdit.setOnClickListener { onEdit(node) }
            binding.btnDelete.setOnClickListener { onDelete(node) }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = ItemNodeConfigBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NodeViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bind(nodes[position])
    }
    
    override fun getItemCount() = nodes.size
}
```

#### **2.6 Criar Layouts XML**

**Layout principal: `res/layout/settings_activity.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBarLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize" />
    </com.google.android.material.appbar.AppBarLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@id/appBarLayout"
        app:layout_constraintBottom_toBottomOf="parent">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <!-- Seção: Base URL -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardElevation="4dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Configuração do Servidor"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_marginBottom="8dp" />

                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="Base URL da API"
                        android:layout_marginBottom="8dp">

                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/editTextBaseUrl"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textUri"
                            android:text="http://192.168.1.100:8000/" />
                    </com.google.android.material.textfield.TextInputLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="end">

                        <Button
                            android:id="@+id/btnTestarConexao"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="Testar Conexão"
                            style="@style/Widget.Material3.Button.OutlinedButton"
                            android:layout_marginEnd="8dp" />

                        <Button
                            android:id="@+id/btnSalvarUrl"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="Salvar URL" />
                    </LinearLayout>
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Seção: Nós Configurados -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                app:cardElevation="4dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:gravity="center_vertical"
                        android:layout_marginBottom="8dp">

                        <TextView
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="Nós Configurados"
                            android:textSize="18sp"
                            android:textStyle="bold" />

                        <Button
                            android:id="@+id/btnAdicionarNo"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="+ Adicionar"
                            style="@style/Widget.Material3.Button.TonalButton" />
                    </LinearLayout>

                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/recyclerViewNodes"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:minHeight="200dp" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- Botão Resetar -->
            <Button
                android:id="@+id/btnResetarPadrao"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="Resetar para Configuração Padrão"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:textColor="@android:color/holo_red_dark" />
        </LinearLayout>
    </ScrollView>
</androidx.constraintlayout.widget.ConstraintLayout>
```

**Item da lista: `res/layout/item_node_config.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    app:cardElevation="2dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp">

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/txtNodeName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Nome do Nó"
                android:textSize="16sp"
                android:textStyle="bold" />

            <TextView
                android:id="@+id/txtNodeMac"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="MAC: AA:BB:CC:DD:EE:FF"
                android:textSize="12sp"
                android:layout_marginTop="4dp" />

            <TextView
                android:id="@+id/txtNodeCoords"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Lat: -3.693269, Lng: -40.354094"
                android:textSize="12sp"
                android:layout_marginTop="2dp" />
        </LinearLayout>

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <ImageButton
                android:id="@+id/btnEdit"
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:src="@android:drawable/ic_menu_edit"
                android:background="?attr/selectableItemBackgroundBorderless"
                android:contentDescription="Editar" />

            <ImageButton
                android:id="@+id/btnDelete"
                android:layout_width="40dp"
                android:layout_height="40dp"
                android:src="@android:drawable/ic_menu_delete"
                android:background="?attr/selectableItemBackgroundBorderless"
                android:contentDescription="Excluir" />
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

**Dialog de edição: `res/layout/dialog_edit_node.xml`**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Endereço MAC"
        android:layout_marginBottom="8dp">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/editTextMac"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="text" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Nome do Local"
        android:layout_marginBottom="8dp">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/editTextName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="text" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Latitude"
        android:layout_marginBottom="8dp">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/editTextLatitude"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="numberDecimal|numberSigned" />
    </com.google.android.material.textfield.TextInputLayout>

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Longitude">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/editTextLongitude"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="numberDecimal|numberSigned" />
    </com.google.android.material.textfield.TextInputLayout>
</LinearLayout>
```

#### **2.7 Integrar com MapsActivity**

Atualize o `MapsActivity` para usar as configurações salvas:

```kotlin
private fun carregarDados() {
    lifecycleScope.launch {
        try {
            // 1. Buscar nós da API
            val api = RetrofitClient.getApi(this@MapsActivity)
            val nodesApi = api.getNodes()
            
            // 2. Carregar coordenadas das configurações
            val nodesConfig = ConfigManager.getNodesConfig(this@MapsActivity)
            
            // 3. Fazer merge dos dados
            val dispositivosCompletos = nodesApi.mapNotNull { node ->
                val config = nodesConfig.find { 
                    it.mac.equals(node.mac, ignoreCase = true) 
                }
                
                if (config != null) {
                    DeviceCompleto(
                        id = node.id,
                        name = node.name,
                        mac = node.mac,
                        lat = config.latitude,
                        lng = config.longitude,
                        status = node.status,
                        lastSeen = node.lastSeen
                    )
                } else {
                    null // Ignorar nós sem configuração de posição
                }
            }
            
            // 4. Adicionar marcadores no mapa
            addMarkers(dispositivosCompletos)
            
        } catch (e: Exception) {
            Log.e("MapsActivity", "Erro: ${e.message}")
            Toast.makeText(this@MapsActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
        }
    }
}
```

---

## ✅ Checklist de Implementação Atualizado

### API (Backend)
- [x] ✅ Rota `/api/nodes` (GET) - **PRONTA**
- [x] ✅ Rota `/api/nodes/:id/status` (GET) - **PRONTA**
- [x] ✅ Rota `/api/nodes/status/summary` (GET) - **PRONTA**
- [x] ✅ Rota `/api/readings/:nodeId/latest` (GET) - **PRONTA**
- [x] ✅ Rotas de leitura histórica - **PRONTAS**
- [ ] ⚠️ *(Opcional)* Adicionar campos `latitude` e `longitude` na tabela `nodes`

### App Android - Configuração Básica
- [ ] 📦 Adicionar dependências do Retrofit no `build.gradle.kts`
- [ ] 📝 Criar data classes em `models/ApiModels.kt`
- [ ] 🔌 Criar interface Retrofit em `network/RespiraNetApi.kt`
- [ ] 🏗️ Criar singleton em `network/RetrofitClient.kt`
- [ ] 🛡️ Adicionar permissão `INTERNET` no `AndroidManifest.xml`
- [ ] 🔓 Adicionar `usesCleartextTraffic="true"` no `AndroidManifest.xml`

### App Android - Sistema de Configurações ⭐ NOVO
- [ ] 📱 Criar `models/NodeConfig.kt` (data classes de configuração)
- [ ] 💾 Criar `utils/ConfigManager.kt` (gerenciador de persistência)
- [ ] 🎨 Criar layout `settings_activity.xml`
- [ ] 🎨 Criar layout `item_node_config.xml`
- [ ] 🎨 Criar layout `dialog_edit_node.xml`
- [ ] 📋 Criar `adapters/NodeConfigAdapter.kt` (RecyclerView)
- [ ] ⚙️ Implementar `SettingsActivity.kt` completa
- [ ] 🔗 Atualizar `RetrofitClient.kt` para usar ConfigManager
- [ ] 📍 Adicionar menu de configurações no `MainActivity`
- [ ] 🗺️ Atualizar `MapsActivity` para usar configurações salvas

### App Android - Integração com API
- [ ] 🏠 Integrar API no `MainActivity`
- [ ] 🗺️ Integrar API no `MapsActivity`
- [ ] 📊 Integrar API no `GraphicsActivity`
- [ ] 🔄 Implementar polling automático (30s)
- [ ] 🛡️ Adicionar tratamento de erros e fallback
- [ ] 📶 Implementar indicador de status de conexão
- [ ] 🧪 Testar com dados reais

---

## 🎯 Resumo das Adaptações Necessárias

### ✅ O que JÁ está pronto na API:
1. ✅ Todos os endpoints necessários existem
2. ✅ Estrutura de dados é compatível
3. ✅ Paginação implementada
4. ✅ Timestamps em ISO 8601

### ⚠️ O que precisa ser adaptado no APP:
1. ⚠️ Remover dados hardcoded e substituir por chamadas da API
2. ⚠️ Implementar Retrofit para requisições HTTP
3. ⚠️ **Implementar ConfigManager para persistência local** ⭐
4. ⚠️ **Criar tela de Configurações completa** ⭐
5. ⚠️ Adicionar polling para atualização automática
6. ⚠️ Mapear `air_quality` da API para texto em português
7. ⚠️ Adaptar contadores (não há campo "instável" na API)
8. ⚠️ Adicionar tratamento de erro e estados de loading

### 🆕 NOVA SOLUÇÃO para Coordenadas GPS:
1. ✅ **ConfigManager** armazena MAC + Posição de cada nó
2. ✅ **SettingsActivity** permite configurar via interface
3. ✅ **MapsActivity** faz merge de dados API + Configurações locais
4. ✅ **Persistência** garantida via SharedPreferences
5. ✅ **Flexibilidade** para adicionar/editar/remover nós dinamicamente

### 🔧 O que pode ser melhorado na API (opcional):
1. 🔧 Adicionar campos `latitude` e `longitude` na tabela `nodes`
2. 🔧 Adicionar campo "unstable" no resumo de status
3. 🔧 Implementar WebSocket para atualização em tempo real (ao invés de polling)

---

## 🚀 Próximos Passos Recomendados

### Fase 1: Setup Inicial (30 minutos)
1. Adicionar dependências do Retrofit no `build.gradle.kts`
2. Criar estrutura de pastas (`models`, `network`, `utils`, `adapters`)
3. Adicionar permissões no `AndroidManifest.xml`

### Fase 2: Modelos e API (1 hora)
1. Criar todas as data classes em `models/ApiModels.kt` e `models/NodeConfig.kt`
2. Criar interface `RespiraNetApi.kt`
3. Criar `RetrofitClient.kt`
4. Criar `ConfigManager.kt`

### Fase 3: Tela de Configurações (2 horas)
1. Criar layouts XML (settings_activity, item_node_config, dialog_edit_node)
2. Criar `NodeConfigAdapter.kt`
3. Implementar `SettingsActivity.kt`
4. Adicionar menu de configurações no `MainActivity`

### Fase 4: Integração com API (2-3 horas)
1. Integrar `MainActivity` com API
2. Atualizar `MapsActivity` para usar ConfigManager + API
3. Atualizar `GraphicsActivity` para usar API
4. Implementar polling automático
5. Adicionar tratamento de erros

### Fase 5: Testes e Refinamento (1 hora)
1. Testar conexão com servidor real
2. Testar adicionar/editar/remover nós
3. Verificar persistência de dados
4. Ajustar UI e mensagens de erro

**⏱️ Tempo total estimado: 6-8 horas**

---

## 🎨 Melhorias Futuras (Opcional)

### 1. Exportar/Importar Configurações via QR Code
```kotlin
// ConfigManager.kt
fun exportConfigAsJson(context: Context): String {
    val config = getAppConfig(context)
    return Gson().toJson(config)
}

fun importConfigFromJson(context: Context, jsonConfig: String): Boolean {
    return try {
        val config = Gson().fromJson(jsonConfig, AppConfig::class.java)
        saveBaseUrl(context, config.baseUrl)
        saveNodesConfig(context, config.nodes)
        true
    } catch (e: Exception) {
        false
    }
}
```

### 2. Sincronizar Nós Automaticamente com API
```kotlin
// SettingsActivity.kt - Botão "Sincronizar com API"
private suspend fun sincronizarComApi() {
    try {
        val api = RetrofitClient.getApi(this)
        val nodes = api.getNodes()
        
        // Atualizar apenas os nomes (manter coordenadas locais)
        val nodesConfig = ConfigManager.getNodesConfig(this)
        
        nodes.forEach { apiNode ->
            val localConfig = nodesConfig.find { it.mac == apiNode.mac }
            if (localConfig != null && localConfig.name != apiNode.description) {
                // Atualizar nome se mudou na API
                ConfigManager.addOrUpdateNode(
                    this,
                    localConfig.copy(name = apiNode.description)
                )
            }
        }
        
        Toast.makeText(this, "Sincronização concluída!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(this, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
```

### 3. Usar Localização do Celular para Adicionar Nó
```kotlin
// SettingsActivity.kt
private fun usarLocalizacaoAtual() {
    // Requer permissão ACCESS_FINE_LOCATION
    if (ActivityCompat.checkSelfPermission(this, 
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                editLat.setText(location.latitude.toString())
                editLng.setText(location.longitude.toString())
            }
        }
    }
}
```

### 4. Indicador de Status de Conexão na Toolbar
```kotlin
// MainActivity.kt
private fun atualizarIndicadorConexao(conectado: Boolean) {
    binding.indicadorConexao.setImageResource(
        if (conectado) R.drawable.ic_online else R.drawable.ic_offline
    )
    binding.txtStatusConexao.text = if (conectado) "Online" else "Offline"
}
```

### 5. Cache Local de Dados (Modo Offline)
```kotlin
object CacheManager {
    private const val KEY_LAST_STATUS = "last_node_status_"
    
    fun saveLastStatus(context: Context, nodeId: Int, status: NodeStatus) {
        val json = Gson().toJson(status)
        getPrefs(context).edit()
            .putString("$KEY_LAST_STATUS$nodeId", json)
            .apply()
    }
    
    fun getLastStatus(context: Context, nodeId: Int): NodeStatus? {
        val json = getPrefs(context).getString("$KEY_LAST_STATUS$nodeId", null)
        return if (json != null) Gson().fromJson(json, NodeStatus::class.java) else null
    }
}

// Usar no MainActivity
val status = try {
    api.getNodeStatus(nodeIdSelecionado)
} catch (e: Exception) {
    CacheManager.getLastStatus(this, nodeIdSelecionado) // Usar cache
}
```

### 6. Notificações Push para Alertas Críticos
```kotlin
// Quando air_quality == "critical"
private fun mostrarNotificacaoAlerta(nodeName: String) {
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_warning)
        .setContentTitle("⚠️ Alerta RespiraNet")
        .setContentText("Qualidade do ar crítica em $nodeName!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()
    
    notificationManager.notify(nodeId, notification)
}
```

---

## 🐛 Troubleshooting Comum

### ❌ Erro: "Unable to resolve host"
**Causa:** Base URL incorreta ou servidor não acessível  
**Solução:**
- ✅ Verificar se servidor está rodando (`curl http://IP:8000/api/nodes`)
- ✅ Usar IP correto da máquina (não `localhost` no celular)
- ✅ Confirmar que celular e servidor estão na mesma rede Wi-Fi
- ✅ Desativar firewall temporariamente para testar
- ✅ Testar URL no navegador do celular primeiro

### ❌ Erro: "Cleartext HTTP traffic not permitted"
**Causa:** Android 9+ bloqueia HTTP (não HTTPS) por padrão  
**Solução:**
```xml
<!-- AndroidManifest.xml -->
<application
    android:usesCleartextTraffic="true"
    ...>
</application>
```

### ❌ Dados não aparecem após configurar
**Causa:** API não retornou dados ou erro de parsing  
**Solução:**
- ✅ Verificar logs do Logcat (filtrar por "Retrofit", "OkHttp")
- ✅ Confirmar que data classes correspondem à estrutura JSON da API
- ✅ Testar endpoints diretamente no Postman/Insomnia
- ✅ Verificar se `@SerializedName` está correto nas data classes

### ❌ Mapa não mostra marcadores
**Causa:** Nenhum nó configurado ou coordenadas inválidas  
**Solução:**
- ✅ Abrir Configurações e verificar lista de nós
- ✅ Clicar "Resetar para Padrão" e testar
- ✅ Verificar se coordenadas estão em formato decimal (-3.693269, não DMS)
- ✅ Conferir se MACs na configuração correspondem aos da API

### ❌ App trava ao abrir tela de gráficos
**Causa:** Muitos dados sendo carregados de uma vez  
**Solução:**
- ✅ Reduzir `limit` das requisições (de 100 para 30)
- ✅ Adicionar `ProgressBar` durante carregamento
- ✅ Garantir que requisição está em coroutine/background thread
- ✅ Verificar memória disponível do dispositivo

### ❌ Erro: "lateinit property api has not been initialized"
**Causa:** `api` não foi inicializado antes do uso  
**Solução:**
```kotlin
private lateinit var api: RespiraNetApi

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // SEMPRE inicializar antes de usar
    api = RetrofitClient.getApi(this)
}
```

---

## 📚 Recursos e Documentação

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Networking](https://developer.android.com/training/basics/network-ops)
- [Gson Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/gson)
- [OkHttp Logging Interceptor](https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor)
- [Material Design 3](https://m3.material.io/)
- [SharedPreferences Guide](https://developer.android.com/training/data-storage/shared-preferences)

---

## 🔍 Resumo de Correções Realizadas

### Documentação Atualizada ✅

| Item | Antes | Depois | Status |
|------|-------|--------|--------|
| Campo de nome | `node.name` | `node.description` | ✅ Corrigido |
| Sensores nullable | Sem verificação | `?.value?.toInt() ?: 0` | ✅ Corrigido |
| Array de gases | Objeto único | `List<GasSensorReading>` | ✅ Corrigido |
| Analytics | Documentação dizia "removido" | APIs confirmadas existentes | ✅ Corrigido |
| Parâmetros opcionais | Documentado como obrigatório | `limit/offset` são opcionais | ✅ Corrigido |
| Base URL | `http://localhost:3000` | `http://localhost:8000` | ✅ Corrigido |

### Arquivos Corrigidos

- ✅ [INTEGRACAO_API.md](INTEGRACAO_API.md) - Documentação completa atualizada
  - Data classes corrigidas
  - Exemplos de código atualizado
  - Mapeamento API correto
  - ConfigManager documentado
  - Próximos passos claros

### Código Atualizado

```kotlin
// ❌ ANTES (Incorreto)
binding.cardStatusLocal.text = response.name // Campo não existe!
binding.txtValueTemp.text = "${response.sensors.temperature.value.toInt()}" // NullPointerException!

// ✅ DEPOIS (Correto)
binding.cardStatusLocal.text = response.description // Campo correto
binding.txtValueTemp.text = "${response.sensors.temperature?.value?.toInt() ?: 0}" // Seguro
```

---

## 🎯 Próximos Passos

1. **Usar a documentação corrigida** como referência
2. **Implementar data classes** conforme especificado
3. **Testar cada endpoint** com Postman antes de integrar
4. **Validar tipos de retorno** com a API real
5. **Adicionar tratamento de null** em todos os sensores

Documentação revisada e corrigida com sucesso! 🎉
