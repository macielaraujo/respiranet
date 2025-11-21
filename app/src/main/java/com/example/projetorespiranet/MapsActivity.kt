package com.example.projetorespiranet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetorespiranet.MapsActivity.EspDevice
import com.example.projetorespiranet.databinding.MapsActivityBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapsActivity : AppCompatActivity() {

    private lateinit var binding: MapsActivityBinding
    private lateinit var map: MapView
    private lateinit var espLeiturasMap: Map<String, EspLeituras>
    data class EspDevice(val id: String, val lat: Double, val lng: Double)
    data class EspLeituras(val id: String, val temp: Double, val umid: Double, val gas: String, val status: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapsActivityBinding.inflate(layoutInflater)
        // Configuração do osmdroid
        org.osmdroid.config.Configuration.getInstance().userAgentValue = packageName
        setContentView(binding.root)

        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(14.0)
        map.controller.setCenter(GeoPoint(-3.686242, -40.358920)) // centro de Sobral

        carregarEspsLeituras()
        carregarEspsMock()

        binding.menuBar.selectedItemId = R.id.menu_maps
        binding.menuBar.setOnItemSelectedListener {
                item -> when(item.itemId){
            R.id.menu_home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            R.id.menu_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
            R.id.menu_graphics -> {
                val intent = Intent(this, GraphicsActivity::class.java)
                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                overridePendingTransition(0, 0)
                true
            }
//            R.id.menu_settings -> {
//                val intent = Intent(this, SettingsActivity::class.java)
//                intent.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//                overridePendingTransition(0, 0)
//                true
//            }
            else -> false
        }

        }

        setSupportActionBar(binding.toolbarMaps)
        supportActionBar?.title = "Ver mapa"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navIcon = binding.toolbarMaps.navigationIcon
        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
        binding.toolbarMaps.navigationIcon = navIcon
        binding.toolbarMaps.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

    }

    private fun carregarEspsMock() {
        val json = assets.open("esps.json").bufferedReader().use { it.readText() }
        val lista = Gson().fromJson(json, Array<EspDevice>::class.java).toList()

        addMarkers(lista)
    }
private fun addMarkers(esps: List<EspDevice>) {
    map.overlays.clear()

    esps.forEach { esp ->
        val marker = Marker(map)
        marker.position = GeoPoint(esp.lat, esp.lng)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = esp.id

        // Encontra a leitura correspondente usando o mapa
        val leituras = espLeiturasMap[esp.id]

        // *** 2. Anexar o objeto de leitura ao marcador ***
        if (leituras != null) {
            marker.setRelatedObject(leituras)
        }

        marker.setOnMarkerClickListener(object : Marker.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {

                // 3. Acessar o ID do dispositivo e o objeto de leitura
                val clickedEspId = marker?.title
                val clickedLeituras = marker?.relatedObject as? EspLeituras // Cast seguro

                // 4. Atualizar os TextViews
                binding.espSelecionada.text = clickedEspId

                if (clickedLeituras != null) {
                    binding.mapTemp.text = clickedLeituras.temp.toString()
                    binding.mapUmidade.text = clickedLeituras.umid.toString()
                    binding.mapGas.text = clickedLeituras.gas
                    binding.mapStatus.text = clickedLeituras.status
                } else {
                    // Limpa ou define um valor padrão se as leituras não forem encontradas
                    binding.mapTemp.text = "N/D"
                    binding.mapUmidade.text = "N/D"
                    // ... (outros)
                }

                // Garante que o balão (InfoWindow) seja exibido, mantendo a experiência de usuário
                marker?.showInfoWindow()

                // Retorna 'true' pois você lidou com o evento de clique
                return true
            }
        })

        map.overlays.add(marker)
    }
    map.invalidate() // atualiza o mapa
}
    private fun carregarEspsLeituras() {
        val json = assets.open("dados.json").bufferedReader().use { it.readText() }
        val leituras = Gson().fromJson(json, Array<EspLeituras>::class.java).toList()

        // *** 1. Mapear a lista de leituras pelo ID ***
        espLeiturasMap = leituras.associateBy { it.id }
    }


}