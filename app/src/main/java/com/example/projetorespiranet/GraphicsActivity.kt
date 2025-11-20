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

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                val novoValor = (5..40).random().toFloat()
                addEntry(novoValor)
                Handler(Looper.getMainLooper()).postDelayed(this, 2000)
            }
        }, 2000)

        setupChart()

        // Lista de dispositivos esp
        val itens = listOf("ESP 1", "ESP 2", "ESP 3")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            itens
        )
        binding.listItemsDevices.adapter = adapter
        binding.listItemsDevices.setOnItemClickListener { _, _, position, _ ->
            val item = itens[position]
            Toast.makeText(this, "Clicou em: $item", Toast.LENGTH_SHORT).show()
        }

        binding.menuBar.selectedItemId = R.id.menu_graphics
        binding.menuBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.menu_maps -> {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.menu_graphics -> {
                    val intent = Intent(this, GraphicsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }

//                R.id.menu_settings -> {
//                    val intent = Intent(this, SettingsActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                    startActivity(intent)
//                    overridePendingTransition(0, 0)
//                    true
//                }

                else -> false
            }
        }

    }

    private fun setupChart() {
        // Lista inicial vazia
        val entries = ArrayList<Entry>()

        lineDataSet1 = LineDataSet(entries, "Medições")
        lineDataSet1.color = Color.BLUE
        lineDataSet1.setCircleColor(Color.BLUE)
        lineDataSet1.lineWidth = 2f
        lineDataSet1.circleRadius = 3f
        lineDataSet1.mode = LineDataSet.Mode.LINEAR
        lineDataSet1.setDrawValues(false)

        lineData1 = LineData(lineDataSet1)

        lineDataSet2 = LineDataSet(entries, "Medições")
        lineDataSet2.color = Color.MAGENTA
        lineDataSet2.setCircleColor(Color.BLUE)
        lineDataSet2.lineWidth = 2f
        lineDataSet2.circleRadius = 3f
        lineDataSet2.mode = LineDataSet.Mode.LINEAR
        lineDataSet2.setDrawValues(false)

        lineData2 = LineData(lineDataSet2)

        binding.chartTemp.apply {
            data = lineData1
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
        }
        binding.chartHumidity.apply {
            data = lineData2
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
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
        lineData1.notifyDataChanged()
        binding.chartTemp.notifyDataSetChanged()
        binding.chartTemp.invalidate()

        lineDataSet2.clear()
        lineData1.notifyDataChanged()
        binding.chartHumidity.notifyDataSetChanged()
        binding.chartHumidity.invalidate()
    }


}