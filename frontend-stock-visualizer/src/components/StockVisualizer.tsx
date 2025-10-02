import React from 'react';
import {
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer,
  BarChart,
  Bar,
  Area,
  AreaChart
} from 'recharts';
import { useWebSocketStable } from '../hooks/useWebSocketStable';
import './StockVisualizer.css';

const StockVisualizer: React.FC = React.memo(() => {
  const { stockData, currentData, connectionStatus } = useWebSocketStable();
  
  // Use stockData directly for charts - no transformation needed
  const chartData = stockData;

  // Static chart configuration to prevent re-creation
  const chartConfig = {
    margin: { top: 10, right: 30, left: 0, bottom: 0 }
  };

  const getConnectionStatusColor = () => {
    if (connectionStatus.connected) return '#4CAF50';
    if (connectionStatus.error) return '#F44336';
    return '#FF9800';
  };

  const formatCurrency = (value: number) => `$${value.toFixed(2)}`;

  return (
    <div className="stock-visualizer">
      <header className="header">
        <h1>Stock Data Visualizer</h1>
        <div className="connection-status">
          <div 
            className="status-indicator"
            style={{ backgroundColor: getConnectionStatusColor() }}
          />
          <span className="status-text">
            {connectionStatus.connected ? 'Connected' : 'Disconnected'}
            {connectionStatus.error && ` - ${connectionStatus.error}`}
          </span>
          {connectionStatus.lastUpdate && (
            <span className="last-update">
              Last update: {connectionStatus.lastUpdate.toLocaleTimeString()}
            </span>
          )}
        </div>
      </header>

      <div className="current-data">
        {currentData ? (
          <div className="data-cards">
            <div className="data-card symbol">
              <h3>Symbol</h3>
              <span className="value">{currentData.symbol}</span>
            </div>
            <div className="data-card high">
              <h3>High</h3>
              <span className="value">{formatCurrency(currentData.high)}</span>
            </div>
            <div className="data-card low">
              <h3>Low</h3>
              <span className="value">{formatCurrency(currentData.low)}</span>
            </div>
            <div className="data-card open">
                <h3>Open</h3>
                <span className="value">{formatCurrency(currentData.open)}</span>
            </div>
            <div className="data-card close">
              <h3>Close</h3>
              <span className="value">{formatCurrency(currentData.close)}</span>
            </div>
            <div className="data-card volume">
              <h3>Volume</h3>
              <span className="value">{currentData.volume?.toLocaleString()}</span>
            </div>
            <div className="data-card debug">
              <h3>Data Points</h3>
              <span className="value">{stockData.length}</span>
            </div>
          </div>
        ) : (
          <div className="no-data">
            <p>Waiting for stock data...</p>
          </div>
        )}
      </div>

      <div className="charts-container">
        <div className="chart-section">
          <h2>Stock Price Trend</h2>
          <ResponsiveContainer key="line-container" width="100%" height={300}>
            <LineChart 
              data={chartData}
              margin={chartConfig.margin}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis 
                dataKey="time" 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10 }}
                interval={"preserveStartEnd"}
                angle={-30}
                textAnchor="end"
                height={60}
              />
              <YAxis 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 12 }}
              />
              <Tooltip 
                formatter={(value, name) => [formatCurrency(Number(value)), name]}
                labelStyle={{ color: '#333' }}
                contentStyle={{ 
                  backgroundColor: 'white', 
                  border: '1px solid #ccc',
                  borderRadius: '4px'
                }}
                isAnimationActive={false}
              />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="high" 
                stroke="#4CAF50" 
                strokeWidth={2}
                dot={false}
                activeDot={false}
                isAnimationActive={false}
                connectNulls={false}
                name="High"
              />
              <Line 
                type="monotone" 
                dataKey="low" 
                stroke="#F44336" 
                strokeWidth={2}
                dot={false}
                activeDot={false}
                isAnimationActive={false}
                connectNulls={false}
                name="Low"
              />
              <Line 
                type="monotone" 
                dataKey="close" 
                stroke="#2196F3" 
                strokeWidth={2}
                dot={false}
                activeDot={false}
                isAnimationActive={false}
                connectNulls={false}
                name="Close"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-section">
          <h2>Price Range (Area Chart)</h2>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart 
              data={chartData}
              margin={chartConfig.margin}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis 
                dataKey="time" 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10 }}
                interval={"preserveStartEnd"}
                angle={-30}
                textAnchor="end"
                height={60}
              />
              <YAxis 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 12 }}
              />
              <Tooltip 
                formatter={(value, name) => [formatCurrency(Number(value)), name]}
                labelStyle={{ color: '#333' }}
                contentStyle={{ 
                  backgroundColor: 'white', 
                  border: '1px solid #ccc',
                  borderRadius: '4px'
                }}
                isAnimationActive={false}
              />
              <Legend />
              <Area 
                type="monotone" 
                dataKey="high" 
                stackId="1" 
                stroke="#4CAF50" 
                fill="#4CAF50" 
                fillOpacity={0.3}
                isAnimationActive={false}
                name="High"
              />
              <Area 
                type="monotone" 
                dataKey="low" 
                stackId="2" 
                stroke="#F44336" 
                fill="#F44336" 
                fillOpacity={0.3}
                isAnimationActive={false}
                name="Low"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>

        <div className="chart-section">
          <h2>Spread Analysis</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart 
              data={chartData}
              margin={chartConfig.margin}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis 
                dataKey="time" 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10 }}
                interval={"preserveStartEnd"}
                angle={-30}
                textAnchor="end"
                height={60}
              />
              <YAxis 
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 12 }}
              />
              <Tooltip 
                formatter={(value) => [formatCurrency(Number(value)), 'Spread']}
                labelStyle={{ color: '#333' }}
                contentStyle={{ 
                  backgroundColor: 'white', 
                  border: '1px solid #ccc',
                  borderRadius: '4px'
                }}
                isAnimationActive={false}
              />
              <Legend />
              <Bar 
                dataKey="spread" 
                fill="#2196F3" 
                name="Spread (High - Low)"
                isAnimationActive={false}
              />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="data-table">
        <h2>Recent Data</h2>
        <table>
          <thead>
            <tr>
              <th>Time</th>
              <th>High</th>
              <th>Low</th>
              <th>Spread</th>
            </tr>
          </thead>
          <tbody>
            {stockData.slice(-10).reverse().map((data) => (
              <tr key={data.id}>
                <td>{data.timestamp.toLocaleTimeString()}</td>
                <td className="high-value">{formatCurrency(data.high)}</td>
                <td className="low-value">{formatCurrency(data.low)}</td>
                <td>{formatCurrency(data.high - data.low)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
});

StockVisualizer.displayName = 'StockVisualizer';
export default StockVisualizer;
