//package com.example.projetorespiranet
//
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.projetorespiranet.MapsActivity.EspDevice
//import com.example.projetorespiranet.databinding.MapsActivityBinding
//import com.example.projetorespiranet.utils.ConfigManager
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.gson.Gson
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//
//class MapsActivity : AppCompatActivity() {
//
//    private lateinit var binding: MapsActivityBinding
//    private lateinit var map: MapView
//    private lateinit var espLeiturasMap: Map<String, EspLeituras>
//    data class EspDevice(val id: String, val lat: Double, val lng: Double)
//    data class EspLeituras(val id: String, val temp: Double, val umid: Double, val gas: String, val status: String)
//
//    private fun abrirTela(destino: Class<*>) {
//        if (this::class.java == destino) return // evita abrir a mesma
//
//        val intent = Intent(this, destino).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//        }
//        startActivity(intent)
//        overridePendingTransition(0, 0)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = MapsActivityBinding.inflate(layoutInflater)
//        // Configuração do osmdroid
//        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName
//        setContentView(binding.root)
//
//        map = binding.map
//        map.setTileSource(TileSourceFactory.MAPNIK)
//        map.setMultiTouchControls(true)
//        map.controller.setZoom(18.0)
//        map.controller.setCenter(GeoPoint(-3.693269, -40.354094)) // centro de Sobral
//
//        carregarEspsLeituras()
//        carregarEspsMock()
//
//
//        binding.menuBar.setOnItemSelectedListener {
//                item -> when (item.itemId) {
//            R.id.menu_home -> {
//                abrirTela(MainActivity::class.java)
//                true
//            }
//            R.id.menu_maps -> {
//                abrirTela(MapsActivity::class.java)
//                true
//            }
//            R.id.menu_graphics -> {
//                abrirTela(GraphicsActivity::class.java)
//                true
//            }
//            else -> false
//        }
//
//        }
//
//        setSupportActionBar(binding.toolbarMaps)
//        supportActionBar?.title = "Ver mapa"
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val navIcon = binding.toolbarMaps.navigationIcon
//        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
//        binding.toolbarMaps.navigationIcon = navIcon
//        binding.toolbarMaps.setNavigationOnClickListener {
//            finish()
//            overridePendingTransition(0,0)
//        }
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//        binding.menuBar.selectedItemId = R.id.menu_maps
//    }
//
//
//    private fun carregarEspsMock() {
//        val json = assets.open("esps.json").bufferedReader().use { it.readText() }
//        val lista = Gson().fromJson(json, Array<EspDevice>::class.java).toList()
//
//        addMarkers(lista)
//    }
//private fun addMarkers(esps: List<EspDevice>) {
//    map.overlays.clear()
//
//    esps.forEach { esp ->
//        val marker = Marker(map)
//        marker.position = GeoPoint(esp.lat, esp.lng)
//        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//        marker.title = esp.id
//
//        // Encontra a leitura correspondente usando o mapa
//        val leituras = espLeiturasMap[esp.id]
//
//        // *** 2. Anexar o objeto de leitura ao marcador ***
//        if (leituras != null) {
//            marker.setRelatedObject(leituras)
//        }
//
//        marker.setOnMarkerClickListener(object : Marker.OnMarkerClickListener {
//            override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
//
//                // 3. Acessar o ID do dispositivo e o objeto de leitura
//                val clickedEspId = marker?.title
//                val clickedLeituras = marker?.relatedObject as? EspLeituras // Cast seguro
//
//                // 4. Atualizar os TextViews
//                binding.espSelecionada.text = clickedEspId
//
//                if (clickedLeituras != null) {
//                    binding.mapTemp.text = clickedLeituras.temp.toString()
//                    binding.mapUmidade.text = clickedLeituras.umid.toString()
//                    binding.mapGas.text = clickedLeituras.gas
//                    binding.mapStatus.text = clickedLeituras.status
//                } else {
//                    // Limpa ou define um valor padrão se as leituras não forem encontradas
//                    binding.mapTemp.text = "N/D"
//                    binding.mapUmidade.text = "N/D"
//                    // ... (outros)
//                }
//
//                // Garante que o balão (InfoWindow) seja exibido, mantendo a experiência de usuário
//                marker?.showInfoWindow()
//
//                // Retorna 'true' pois você lidou com o evento de clique
//                return true
//            }
//        })
//
//        map.overlays.add(marker)
//    }
//    map.invalidate() // atualiza o mapa
//}
////    private fun carregarEspsLeituras() {
////        val json = assets.open("dados.json").bufferedReader().use { it.readText() }
////        val leituras = Gson().fromJson(json, Array<EspLeituras>::class.java).toList()
////
////        // *** 1. Mapear a lista de leituras pelo ID ***
////        espLeiturasMap = leituras.associateBy { it.id }
////    }
//
//    private fun carregarEspsConfig() {
//        // Busca a lista de nós salvos no ConfigManager (SharedPreferences)
//        val listaNodesConfig = ConfigManager.getNodesConfig(this)
//
//        // Converte para o formato que seu marcador espera
//        val listaMarkers = listaNodesConfig.map { config ->
//            EspDevice(id = config.name, lat = config.latitude, lng = config.longitude)
//        }
//
//        addMarkers(listaMarkers)
//    }
//
//
//}

package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projetorespiranet.databinding.MapsActivityBinding
import com.example.projetorespiranet.models.NodeStatus
import com.example.projetorespiranet.network.RetrofitClient
import com.example.projetorespiranet.utils.ConfigManager
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: MapsActivityBinding
    private lateinit var map: MapView

    // Agora usamos o modelo NodeStatus vindo da API para os dados dinâmicos
    private var espLeiturasMap = mutableMapOf<String, NodeStatus>()

    // Estrutura interna para facilitar a plotagem dos marcadores
    data class MarkerData(val name: String, val mac: String, val lat: Double, val lng: Double)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapsActivityBinding.inflate(layoutInflater)
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName
        setContentView(binding.root)

        configurarMapa()
        configurarToolbar()
        configurarNavegacao()

        // Inicia o fluxo de dados
        carregarConfiguracoesELeturas()
    }

    private fun configurarMapa() {
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)
        map.controller.setCenter(GeoPoint(-3.693269, -40.354094)) // Sobral
    }

    private fun carregarConfiguracoesELeturas() {
        // 1. Pegamos os locais e coordenadas salvos no ConfigManager
        val nodesConfig = ConfigManager.getNodesConfig(this)

        val listaMarkers = nodesConfig.map { config ->
            MarkerData(config.name, config.mac, config.latitude, config.longitude)
        }

        // 2. Criamos os marcadores no mapa
        addMarkers(listaMarkers)

        // 3. Buscamos o status em tempo real de cada MAC via API
        atualizarStatusViaApi(listaMarkers)
    }

    private fun atualizarStatusViaApi(lista: List<MarkerData>) {
        lifecycleScope.launch {
            val api = RetrofitClient.getApi(this@MapsActivity)
            lista.forEach { item ->
                try {
                    // Como a API usa IDs numéricos, se você tiver o ID no NodeConfig use-o,
                    // aqui simularemos buscando pelo ID fixo ou mapeado
                    // Se sua API permitir busca por MAC, use a rota correspondente.
                    // Exemplo usando ID 1, 2, 3 baseado na ordem:
                    val nodeId = lista.indexOf(item) + 1
                    val status = api.getNodeStatus(nodeId)

                    // Armazenamos o status usando o nome como chave para o clique no marcador
                    espLeiturasMap[item.name] = status
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun addMarkers(markersData: List<MarkerData>) {
        map.overlays.clear()

        markersData.forEach { data ->
            val marker = Marker(map)
            marker.position = GeoPoint(data.lat, data.lng)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = data.name

            marker.setOnMarkerClickListener { m, _ ->
                binding.espSelecionada.text = m.title

                val status = espLeiturasMap[m.title]
                if (status != null) {
                    // Preenche com dados REAIS da API (usando os modelos novos)
                    binding.mapTemp.text = "${status.sensors.temperature?.value ?: "--"}°C"
                    binding.mapUmidade.text = "${status.sensors.humidity?.value ?: "--"}%"
                    binding.mapGas.text = status.airQuality ?: "N/D"
                    binding.mapStatus.text = status.status
                } else {
                    binding.mapTemp.text = "Carregando..."
                    binding.mapStatus.text = "Sem conexão"
                }

                m.showInfoWindow()
                true
            }
            map.overlays.add(marker)
        }
        map.invalidate()
    }

    private fun configurarNavegacao() {
        binding.menuBar.selectedItemId = R.id.menu_maps
        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> { abrirTela(MainActivity::class.java); true }
                R.id.menu_graphics -> { abrirTela(GraphicsActivity::class.java); true }
                else -> false
            }
        }
    }

    private fun abrirTela(destino: Class<*>) {
        if (this::class.java == destino) return
        startActivity(Intent(this, destino).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
        overridePendingTransition(0, 0)
    }

    private fun configurarToolbar() {
        setSupportActionBar(binding.toolbarMaps)
        supportActionBar?.title = "Mapa de Monitoramento"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarMaps.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        binding.menuBar.selectedItemId = R.id.menu_maps
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}