# market-anomaly-detector

A Spring Boot backend for real-time stock anomaly detection powered by [Salesforce Merlion](https://github.com/salesforce/Merlion). Designed as a prototype for **QuoteMedia**, this project analyzes price data streams to detect outliers, system irregularities, and potential market anomalies using AI models hosted in a companion Python service.

---

## What does this do?

This backend:

- Consumes tick data (timestamp, price, symbol)
- Sends it to a Python service running Merlion for anomaly detection
- Logs and exports anomalies with relevant metadata
- Offers a simple HTTP API for querying status, exporting results, and monitoring symbol health

> Used to prototype QuoteStream anomaly infrastructure  
> Powered by machine learning from Merlion (SpectralResidual, Thresholding, Normalization, etc.)

---

## How it works
```text
[QuoteStream / CSV input]
          ↓
   TickProcessor.java
          ↓
MerlionClient.java ──→ [Python FastAPI + Merlion Service]
          ↓
AnomalyService.java (stores state, logs anomalies, status)
          ↓
   ExportController / StatusController
```

- **MerlionClient** sends price and timestamp series to the Merlion FastAPI endpoint (`/detect`)
- Receives anomaly scores and binary labels
- **AnomalyService** logs results and updates in-memory symbol states
- Exportable daily logs, status queries per symbol

```text
src/
└── main/
    └── java/com/quotemediaexample/demo/
        ├── controller/
        │   ├── ExportController.java         # GET /anomaly/v1/export/daily
        │   └── StatusController.java         # GET /status/{symbol}
        │
        ├── model/
        │   ├── AnomalyState.java             # Enum for symbol state (NORMAL, SUSPECT, CONFIRMED)
        │   └── Tick.java                     # Timestamped price data
        │
        ├── service/
        │   ├── AnomalyService.java           # Core state tracking & daily CSV persistence
        │   ├── MerlionClient.java            # Sends HTTP POSTs to Merlion
        │   ├── SlackNotifier.java            # (Optional) Notifies anomalies to Slack
        │   ├── SymbolState.java              # Sliding window per-symbol z-score state
        │   └── TickProcessor.java            # Ingests test CSV and triggers pipeline
        │
        ├── util/
        │   └── ZScoreWindow.java             # [LEGACY] Not used with Merlion, can be removed
        │
        └── QuotesAnomolyDemoApplication.java # Spring Boot entry point
```

Merlion Integration
The anomaly logic is offloaded to a separate Python FastAPI service which is [located here](https://github.com/gaurabacharya/merlion-anomaly-service):

Accepts JSON with timestamps, prices, and symbol

Returns anomaly_scores and labels

Uses SpectralResidual + Thresholding for detection

The Java backend does not train or store models, keeping compute light and portable.

## 🧪 How to Run Locally

### 1. Prereqs

- Java 17+
- Maven
- Python 3.10+
- Clone the Merlion service repo separately

```bash
git clone https://github.com/gaurabacharya/merlion-anomaly-service.git
cd merlion-service
pip install -r requirements.txt
python merlion_server.py  # runs on port 8001
```
### 2. Start this Spring Boot App
```bash
git clone https://github.com/gaurabacharya/market-anomaly-detector.git
cd market-anomaly-detector
mvn spring-boot:run
```
Visit: http://localhost:8080

### Endpoints
- GET	/status/{symbol}	Returns last known status of a symbol - right now APPL is only being used
- GET	/anomaly/v1/export/daily	Export all anomaly records for the day

## Future Scaling
- Database: Persist anomaly logs (PostgreSQL, MongoDB)
- Real-time Kafka pipeline: Consume from QuoteStream or real feeds
- APIs: Expand to serve frontend dashboards or notification tools
- Model choices: Integrate other detectors like Prophet, LSTM, Isolation Forest
