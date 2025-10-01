# Stock Data Visualizer

A real-time React frontend application that visualizes stock data consumed from a Kafka producer. The application displays high/low stock values in multiple chart formats with real-time updates.

## Features

- **Real-time Data Visualization**: Live charts showing High/Low stock prices
- **Multiple Chart Types**: Line charts, area charts, and bar charts for spread analysis
- **Connection Status**: Real-time connection status indicator
- **Data Table**: Recent stock data in tabular format
- **Responsive Design**: Works on desktop and mobile devices
- **Kafka Integration**: Consumes data from Kafka topics
- **Data Simulation**: Falls back to simulated data when Kafka is unavailable

## Data Format

The application expects JSON data in the following format:

```json
{
  "High": 100,
  "Low": 78
}
```

## Project Structure

```
frontend-stock-visualizer/
├── src/
│   ├── components/
│   │   ├── StockVisualizer.tsx
│   │   └── StockVisualizer.css
│   ├── hooks/
│   │   └── useWebSocket.ts
│   ├── types/
│   │   └── stockTypes.ts
│   ├── App.tsx
│   ├── App.css
│   └── index.tsx
├── server/
│   ├── server.js
│   └── package.json
├── public/
└── README.md
```

## Setup Instructions

### Prerequisites

- Node.js (v14 or higher)
- npm or yarn
- Kafka cluster (optional - app will simulate data if unavailable)

### Frontend Setup

1. **Install dependencies**:
   ```bash
   npm install
   ```

2. **Start the React development server**:
   ```bash
   npm start
   ```

   The app will run on `http://localhost:3000`

### Backend Setup

1. **Navigate to the server directory**:
   ```bash
   cd server
   ```

2. **Install server dependencies**:
   ```bash
   npm install
   ```

3. **Start the WebSocket server**:
   ```bash
   npm start
   ```

   The server will run on `http://localhost:3001`

## Configuration

### Environment Variables

You can configure the backend server using environment variables:

- `PORT`: Server port (default: 3001)
- `KAFKA_BROKERS`: Comma-separated list of Kafka brokers (default: localhost:9092)
- `KAFKA_TOPIC`: Kafka topic name (default: stock-data)
- `SIMULATE_DATA`: Set to 'false' to disable data simulation (default: true)
- `DEMO_MODE`: Set to 'false' to disable demo mode (default: true)

### Example Environment Setup

Create a `.env` file in the server directory:

```env
PORT=3001
KAFKA_BROKERS=localhost:9092,localhost:9093
KAFKA_TOPIC=stock-prices
SIMULATE_DATA=true
```

## Quick Demo Start

### One-Click Demo Launch

For the easiest demo experience, use the provided launcher scripts:

**Windows (PowerShell - Recommended):**
```powershell
.\start-demo.ps1
```

**Windows (Command Prompt):**
```cmd
start-demo.bat
```

These scripts will:
- Install dependencies automatically if needed
- Start the backend server in demo mode
- Start the React frontend
- Open your browser to the application
- Generate random stock data (1-100) every second

### Manual Demo Start

1. **Start the backend server:**
   ```bash
   cd server
   npm install
   npm start
   ```

2. **Start the frontend (in a new terminal):**
   ```bash
   npm install
   npm start
   ```

3. **Open your browser to:** `http://localhost:3000`

The demo will immediately start generating random stock data with High/Low values between 1-100 every second.

## Usage

### Running with Kafka

1. **Start your Kafka cluster**
2. **Create a topic** for stock data:
   ```bash
   kafka-topics --create --topic stock-data --bootstrap-server localhost:9092
   ```
3. **Produce data** to the topic in the expected JSON format:
   ```bash
   kafka-console-producer --topic stock-data --bootstrap-server localhost:9092
   ```
4. **Start the backend server**:
   ```bash
   cd server && npm start
   ```
5. **Start the React app**:
   ```bash
   npm start
   ```

### Running with Simulated Data

If Kafka is not available, the server automatically falls back to simulating stock data:

1. **Start the backend server**:
   ```bash
   cd server && npm start
   ```
2. **Start the React app**:
   ```bash
   npm start
   ```

The server will generate random stock data every 2 seconds.

## API Endpoints

The backend server provides the following endpoints:

- `GET /health`: Health check and status information
- `GET /config`: Current server configuration

### Example Health Response

```json
{
  "status": "ok",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "kafka_connected": false,
  "simulating": true,
  "connected_clients": 1
}
```

## Technology Stack

### Frontend
- **React 18** with TypeScript
- **Recharts** for data visualization
- **Socket.io-client** for WebSocket connections
- **CSS Grid/Flexbox** for responsive layouts

### Backend
- **Node.js** with Express
- **Socket.io** for WebSocket communication
- **KafkaJS** for Kafka integration
- **CORS** for cross-origin requests

## Development

### Running in Development Mode

1. **Start backend with auto-reload**:
   ```bash
   cd server && npm run dev
   ```

2. **Start frontend**:
   ```bash
   npm start
   ```

### Building for Production

1. **Build the React app**:
   ```bash
   npm run build
   ```

2. **Start production server**:
   ```bash
   cd server && npm start
   ```

## Troubleshooting

### Connection Issues

- Ensure the backend server is running on port 3001
- Check CORS configuration if accessing from different domains
- Verify WebSocket URL in the React app matches the server

### Kafka Issues

- Verify Kafka brokers are accessible
- Check topic exists and has the correct name
- Ensure data format matches expected JSON structure
- The app will fall back to simulation if Kafka is unavailable

### Performance Issues

- The app keeps only the last 100 data points for performance
- Consider implementing data aggregation for high-frequency updates

## License

MIT License
