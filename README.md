# Projeto RespiraNet — Monitoramento de Ambientes com ESP32

O **RespiraNet** é um aplicativo Android desenvolvido para monitoramento em tempo real de ambientes utilizando sensores conectados a módulos **ESP32**.  
O sistema coleta dados de **temperatura, umidade, nível de gases e luminosidade**, enviando-os para o app, onde são exibidos de forma organizada, visual e interativa.

O objetivo é fornecer uma ferramenta simples, eficiente e de baixo custo para acompanhamento de condições ambientais em residências, escritórios, indústrias, estufas, data centers e muito mais.

---

## Funcionalidades Principais

### 🔹 Dashboard em Tempo Real
- Leituras atualizadas de:
  - Temperatura  
  - Umidade  
  - Gases  
  - Luminosidade  
- Interface simples e moderna.

### 🔹 Gráficos Dinâmicos (MPAndroidChart)
- Gráficos de linha atualizados automaticamente.
- Visualização de histórico das leituras.
- Suporte a atualizações em tempo real.

### 🔹 Mapa de Dispositivos (OSMDroid)
- Utiliza OpenStreetMap (gratuito).
- Exibe a posição de cada ESP32.
- Marcadores com identificação e últimos dados.

### 🔹 Dados Mockados para Testes
- Sistema pode carregar dados a partir de um arquivo JSON em `assets/`.
- Facilita desenvolvimento sem servidor ativo.

### 🔹 Navegação Moderna
- BottomNavigationView com:
  - Home  
  - Mapa  
  - Gráficos  
  - Configurações  

---

## 🧱 Arquitetura do Projeto

O app foi construído em **Kotlin**, aplicando boas práticas:

- ViewBinding  
- Activities independentes  
- Dados mockados via JSON  
- Organização modular  
- Mapa com OSMDroid  
- Gráficos com MPAndroidChart  

---

## 🛠️ Tecnologias Utilizadas

### Android
- Kotlin  
- ViewBinding  
- ConstraintLayout  
- Material Design  
- MPAndroidChart  
- OSMDroid  
- Gson  

### ESP32
- Leitura de sensores ambientais  
- Envio de dados via HTTP/JSON  
- Configuração dinâmica de Wi-Fi via SPIFFS/LittleFS  

---

## 📂 Estrutura Simplificada do App

```
📦 /app
┣ 📂 java/com.example.projetorespiranet
│ ┣ MainActivity.kt
│ ┣ MapsActivity.kt
│ ┣ GraphicsActivity.kt
│ ┣ SettingsActivity.kt
│ ┗ adapters/, models/, utils/
┣ 📂 res
│ ┣ layout/
│ ┣ values/
│ ┣ drawable/
│ ┗ menu/
┗ 📂 assets
└ esps.json
```


---

## 🌍 Mapa com OSMDroid

O app usa mapas **gratuitos** via OSMDroid.

Exemplo de configuração:

```kotlin
map.setTileSource(TileSourceFactory.MAPNIK)
map.controller.setZoom(5.0)
map.controller.setCenter(GeoPoint(-3.686242, -40.358920))
```
---

## 📊 Gráficos de Monitoramento

- Construídos com MPAndroidChart, permitem:

- Atualização em tempo real

- Zoom e pan

- Linhas suaves

```
val entry = Entry(xIndex.toFloat(), newValue.toFloat())
lineDataSet.addEntry(entry)
lineChart.notifyDataSetChanged()
lineChart.invalidate()
```
---

## 🚀 Como Executar o Projeto

1. Clone o repositório:
git clone https://github.com/seu-usuario/respiranet.git

2. Abra no Android Studio
```
File → Open → selecione a pasta do projeto
```
3. Execute o app

Use um dispositivo físico ou emulador

4. Dados mockados

Certifique-se de que assets/esps.json existe.

---

## 📌 Dependências Principais (Gradle)
```
implementation 'com.github.MikePhil.Charting:MPAndroidChart:v3.1.0'
implementation 'org.osmdroid:osmdroid-android:6.1.15'
implementation 'com.google.code.gson:gson:2.10.1'
```

---

## 🧪 Próximos passos

- Integração com servidor real

- Suporte a MQTT

- Alertas e notificações

- Modo escuro

- Suporte a múltiplos ambientes

---

## 📄 Licença

Distribuído sob licença MIT.

## 👨‍💻 Desenvolvedor

- Maciel Araújo
- Mauro Furtado
- Darlyson Fontenele
  
