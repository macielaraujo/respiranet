package com.example.projetorespiranet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.projetorespiranet.databinding.GraphicsActivityBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomnavigation.BottomNavigationView

class GraphicsActivity : AppCompatActivity() {

    private lateinit var binding: GraphicsActivityBinding
    // Dataset principal
    private lateinit var lineDataSet1: LineDataSet
    private lateinit var lineDataSet2: LineDataSet
    // Objeto do gráfico
    private lateinit var lineData1: LineData
    private lateinit var lineData2: LineData

    private var dispositivoSelecionado: String = ""

    private fun abrirTela(destino: Class<*>) {
        if (this::class.java == destino) return // evita abrir a mesma

        val intent = Intent(this, destino).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GraphicsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarGraphics)
        supportActionBar?.title = "Visualização Gráfica"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navIcon = binding.toolbarGraphics.navigationIcon
        navIcon?.setBounds(0, 0, 10, 10) // largura e altura em pixels
        binding.toolbarGraphics.navigationIcon = navIcon
        binding.toolbarGraphics.setNavigationOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

//        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
//            override fun run() {
//                val novoValor = (5..40).random().toFloat()
//                addEntry(novoValor)
//                Handler(Looper.getMainLooper()).postDelayed(this, 2000)
//            }
//        }, 2000)

        setupChart()
        configurarSeletor()
        iniciarSimulacaoDeDados()
        aplicarTemaAoGrafico(binding.chartTemp)
        aplicarTemaAoGrafico(binding.chartHumidity)




        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    abrirTela(MainActivity::class.java)
                    true
                }
                R.id.menu_maps -> {
                    abrirTela(MapsActivity::class.java)
                    true
                }
                R.id.menu_graphics -> {
                    abrirTela(GraphicsActivity::class.java)
                    true
                }
                else -> false
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.menuBar.selectedItemId = R.id.menu_graphics
    }

    private fun setupChart() {
        val entries1 = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()

        // Configuração Dataset 1 (Temperatura - Laranja)
        lineDataSet1 = LineDataSet(entries1, "Temperatura (°C)")
        lineDataSet1.apply {
            color = Color.parseColor("#FF9800") // Laranja vibrante
            setCircleColor(Color.parseColor("#FF9800"))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(true)
            circleHoleColor = Color.TRANSPARENT // Efeito vazado
            mode = LineDataSet.Mode.CUBIC_BEZIER // Linha suave/curvada
            setDrawValues(false)
        }

        // Configuração Dataset 2 (Umidade - Ciano)
        lineDataSet2 = LineDataSet(entries2, "Umidade (%)")
        lineDataSet2.apply {
            color = Color.parseColor("#00BCD4") // Ciano vibrante
            setCircleColor(Color.parseColor("#00BCD4"))
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(true)
            circleHoleColor = Color.TRANSPARENT
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
        }

        lineData1 = LineData(lineDataSet1)
        lineData2 = LineData(lineDataSet2)

        binding.chartTemp.apply {
            data = lineData1
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            animateX(1000) // Animação ao abrir
        }

        binding.chartHumidity.apply {
            data = lineData2
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            animateX(1000)
        }
    }

    // A função de aplicar tema permanece igual, mas agora é chamada no onCreate
    fun aplicarTemaAoGrafico(chart: LineChart) {
        val isDarkMode = (resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        // No modo escuro usamos branco, no claro usamos um cinza escuro para melhor legibilidade
        val corTexto = if (isDarkMode) Color.WHITE else Color.DKGRAY
        // Grade sutil
        val corGrade = if (isDarkMode) Color.parseColor("#22FFFFFF") else Color.parseColor("#11000000")

        chart.apply {
            xAxis.textColor = corTexto
            xAxis.gridColor = corGrade

            axisLeft.textColor = corTexto
            axisLeft.gridColor = corGrade

            axisRight.isEnabled = false // Desabilita o eixo da direita para visual mais limpo

            legend.textColor = corTexto
            invalidate()
        }
    }
    fun addEntry(value: Float) {
        val index1 = lineDataSet1.entryCount // posição X automaticamente
        val index2 = lineDataSet2.entryCount // posição X automaticamente

        val newEntry1 = Entry(index1.toFloat(), value)
        lineDataSet1.addEntry(newEntry1)

        val newEntry2 = Entry(index2.toFloat(), value)
        lineDataSet2.addEntry(newEntry2)

        lineData1.notifyDataChanged()     // Notifica o dataset
        binding.chartTemp.notifyDataSetChanged() // Notifica o gráfico
        binding.chartTemp.invalidate()   // Redesenha

        lineData2.notifyDataChanged()     // Notifica o dataset
        binding.chartHumidity.notifyDataSetChanged() // Notifica o gráfico
        binding.chartHumidity.invalidate()   // Redesenha
    }
    fun resetChart() {
        lineDataSet1.clear()
        lineDataSet2.clear()

        lineData1.notifyDataChanged()
        lineData2.notifyDataChanged()

        binding.chartTemp.notifyDataSetChanged()
        binding.chartHumidity.notifyDataSetChanged()

        binding.chartTemp.invalidate()
        binding.chartHumidity.invalidate()
    }

    private fun configurarSeletor() {
        val itens = listOf("Restaurante Universitário", "Bloco 1: Merendeiro", "Bloco 2: Odontologia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, itens)

        binding.autoCompleteTextView.setAdapter(adapter)

        // Evento de seleção (O "Select" do Android)
        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            dispositivoSelecionado = itens[position]

            // 1. Limpa o gráfico atual para renderizar o novo
            resetChart()

            // 2. Simula o carregamento de dados históricos específicos (opcional)
            gerarDadosIniciaisParaDispositivo(dispositivoSelecionado)

            Toast.makeText(this, "Monitorando: $dispositivoSelecionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun gerarDadosIniciaisParaDispositivo(nome: String) {
        // Aqui você poderia carregar dados reais.
        // Vamos apenas gerar 5 pontos aleatórios para dar a sensação de mudança.
        for (i in 1..5) {
            val valorFake = when (nome) {
                "Restaurante Universitário" -> (30..40).random().toFloat()
                "Bloco 1: Merendeiro" -> (20..25).random().toFloat()
                else -> (15..20).random().toFloat()
            }
            addEntry(valorFake)
        }
    }

    private fun iniciarSimulacaoDeDados() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                // Só adiciona dados se um dispositivo estiver selecionado
                if (dispositivoSelecionado.isNotEmpty()) {
                    val novoValor = (10..45).random().toFloat()
                    addEntry(novoValor)
                }
                handler.postDelayed(this, 2000)
            }
        })
    }
}