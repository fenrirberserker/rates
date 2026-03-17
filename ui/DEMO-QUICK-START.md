# 🚀 Stock Visualizer Demo - Quick Start

## Instant Demo Launch

### Option 1: PowerShell Script (Recommended)
```powershell
.\start-demo.ps1
```

### Option 2: Command Prompt
```cmd
start-demo.bat
```

### Option 3: Manual Launch
```bash
# Terminal 1 - Backend
cd server
npm install
npm start

# Terminal 2 - Frontend  
npm install
npm start
```

## What You'll See

✅ **Backend Server** starts on port 3001  
✅ **React Frontend** opens at http://localhost:3000  
✅ **Random Stock Data** generated every second  
✅ **Values range** from 1-100 for High and Low  
✅ **Real-time Charts** update automatically  
✅ **Connection Status** shows "Connected"  

## Demo Features

### 📊 Visualizations
- **Line Chart**: High/Low trends over time
- **Area Chart**: Price range visualization  
- **Bar Chart**: Spread analysis (High - Low)
- **Data Table**: Recent transactions

### 🔄 Real-time Updates
- New data every **1 second**
- **100 data points** kept in memory
- **WebSocket connection** for live updates
- **Auto-reconnection** if connection drops

### 📱 Responsive Design
- Works on **desktop and mobile**
- **Grid layout** adapts to screen size
- **Touch-friendly** on mobile devices

## Health Check

Visit: http://localhost:3001/health

Expected response:
```json
{
  "status": "ok",
  "demo_mode": true,
  "simulating": true,
  "data_interval": "1 second",
  "value_range": "1-100",
  "connected_clients": 1
}
```

## Data Format

The dummy service generates data in this format:
```json
{
  "High": 87,
  "Low": 42
}
```

## Stopping the Demo

- **Close browser tab** (frontend)
- **Ctrl+C** in server terminal (backend)
- **Close terminal windows**

## Next Steps

1. **Customize data range**: Edit `simulateStockData()` in `server/server.js`
2. **Change update frequency**: Modify interval in `startSimulation()`
3. **Connect to real Kafka**: Set environment variables for production
4. **Add more visualizations**: Extend the React components

## Troubleshooting

❌ **Port 3000/3001 already in use**
- Stop other applications using these ports
- Or change ports in server.js and React app

❌ **Dependencies missing**
- Run `npm install` in both root and server directories

❌ **Browser doesn't open automatically**  
- Manually navigate to http://localhost:3000

---

**Enjoy exploring the real-time stock data visualization!** 📈