import React, { useMemo, useState, useCallback } from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { useWebSocketStable } from '../hooks/useWebSocketStable';
import { StockData } from '../types/stockTypes';
import './StockVisualizer.css';

const formatCurrency = (value: number) => `$${value.toFixed(2)}`;

// OHLCV lines — each has a fixed semantic color
const OHLCV_LINES = [
  { key: 'high',  label: 'High',   color: '#4db876' },
  { key: 'low',   label: 'Low',    color: '#e05c5c' },
  { key: 'open',  label: 'Open',   color: '#e09e4d' },
  { key: 'close', label: 'Close',  color: '#5b9cf6' },
];

// ----------------------------------------------------------------
// DataLabels — OHLCV stat strip rendered below the chart
// ----------------------------------------------------------------
const DataLabels: React.FC<{ data: StockData }> = React.memo(({ data }) => (
  <div className="data-labels">
    <div className="data-label">
      <span className="data-label-key">High</span>
      <span className="data-label-value high-value">{formatCurrency(data.high)}</span>
    </div>
    <div className="data-label">
      <span className="data-label-key">Low</span>
      <span className="data-label-value low-value">{formatCurrency(data.low)}</span>
    </div>
    <div className="data-label">
      <span className="data-label-key">Open</span>
      <span className="data-label-value open-value">{formatCurrency(data.open)}</span>
    </div>
    <div className="data-label">
      <span className="data-label-key">Close</span>
      <span className="data-label-value close-value">{formatCurrency(data.close)}</span>
    </div>
    <div className="data-label">
      <span className="data-label-key">Volume</span>
      <span className="data-label-value">{data.volume?.toLocaleString()}</span>
    </div>
  </div>
));
DataLabels.displayName = 'DataLabels';

// ----------------------------------------------------------------
// SymbolChart — chart + data labels for the active symbol
// ----------------------------------------------------------------
const SymbolChart: React.FC<{ symbol: string; history: StockData[]; latest: StockData | null }> =
  React.memo(({ symbol, history, latest }) => {
    const chartData = useMemo(
      () => history.map(d => ({ time: d.time, high: d.high, low: d.low, open: d.open, close: d.close })),
      [history]
    );

    return (
      <div className="symbol-view">
        <h3 className="symbol-view-title">{symbol}</h3>

        <div className="chart-section">
          <ResponsiveContainer width="100%" height={320}>
            <LineChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#2a2a2a" />
              <XAxis
                dataKey="time"
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 10, fill: '#888' }}
                interval="preserveStartEnd"
                angle={-30}
                textAnchor="end"
                height={50}
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tick={{ fontSize: 11, fill: '#888' }}
                tickFormatter={(v) => `$${v}`}
              />
              <Tooltip
                formatter={(value, name) => [formatCurrency(Number(value)), name]}
                isAnimationActive={false}
                contentStyle={{ backgroundColor: '#1e1e1e', border: '1px solid #333', borderRadius: '6px', color: '#e0e0e0' }}
                labelStyle={{ color: '#aaa' }}
              />
              <Legend wrapperStyle={{ color: '#aaa', fontSize: '0.85rem' }} />
              {OHLCV_LINES.map(({ key, label, color }) => (
                <Line
                  key={key}
                  type="monotone"
                  dataKey={key}
                  stroke={color}
                  strokeWidth={2}
                  dot={false}
                  activeDot={false}
                  isAnimationActive={false}
                  name={label}
                />
              ))}
            </LineChart>
          </ResponsiveContainer>
        </div>

        {latest && <DataLabels data={latest} />}
      </div>
    );
  });
SymbolChart.displayName = 'SymbolChart';

// ----------------------------------------------------------------
// StockVisualizer — root component
// ----------------------------------------------------------------
const StockVisualizer: React.FC = React.memo(() => {
  const { historyBySymbol, latestBySymbol, connectionStatus } = useWebSocketStable();

  const [activeType, setActiveType]     = useState<string | null>(null);
  const [activeSymbol, setActiveSymbol] = useState<string | null>(null);

  const handleTypeClick = useCallback((type: string) => {
    setActiveType(prev => prev === type ? prev : type);
    setActiveSymbol(null);
  }, []);

  // Types in order of first appearance, derived from per-symbol history keys
  const types = useMemo(() => {
    const seen = new Set<string>();
    const order: string[] = [];
    for (const history of Object.values(historyBySymbol)) {
      if (history.length > 0) {
        const t = history[0].type;
        if (!seen.has(t)) { seen.add(t); order.push(t); }
      }
    }
    return order;
  }, [historyBySymbol]);

  // Symbols for the active type, in order of first appearance
  const activeTypeSymbols = useMemo(() => {
    if (!activeType) return [];
    return Object.keys(historyBySymbol).filter(
      sym => historyBySymbol[sym]?.[0]?.type === activeType
    );
  }, [historyBySymbol, activeType]);

  // Full history for the active symbol — preserved across tab switches
  const activeHistory = useMemo(
    () => (activeSymbol ? historyBySymbol[activeSymbol] ?? [] : []),
    [historyBySymbol, activeSymbol]
  );

  // Latest data point for the active symbol
  const activeLatest = activeSymbol ? latestBySymbol[activeSymbol] ?? null : null;

  const statusColor = connectionStatus.connected
    ? '#4db876'
    : connectionStatus.error ? '#e05c5c' : '#e09e4d';

  return (
    <div className="stock-visualizer">
      <header className="header">
        <h1>Stock Data Visualizer</h1>
        <div className="connection-status">
          <div className="status-indicator" style={{ backgroundColor: statusColor }} />
          <span className="status-text">
            {connectionStatus.connected ? 'Connected' : 'Disconnected'}
            {connectionStatus.error && ` — ${connectionStatus.error}`}
          </span>
          {connectionStatus.lastUpdate && (
            <span className="last-update">
              Last update: {connectionStatus.lastUpdate.toLocaleTimeString()}
            </span>
          )}
        </div>
      </header>

      {types.length === 0 ? (
        <div className="no-data"><p>Waiting for stock data…</p></div>
      ) : (
        <div className="main-panel">
          {/* Type tabs */}
          <div className="type-tabs">
            {types.map(type => (
              <button
                key={type}
                className={`type-tab${activeType === type ? ' active' : ''}`}
                onClick={() => handleTypeClick(type)}
              >
                {type}
              </button>
            ))}
          </div>

          {/* Symbol list */}
          {activeType && (
            <div className="symbol-list">
              {activeTypeSymbols.length === 0 ? (
                <span className="symbol-list-empty">Waiting for {activeType.toLowerCase()} symbols…</span>
              ) : (
                activeTypeSymbols.map(sym => (
                  <button
                    key={sym}
                    className={`symbol-btn${activeSymbol === sym ? ' active' : ''}`}
                    onClick={() => setActiveSymbol(sym)}
                  >
                    {sym}
                  </button>
                ))
              )}
            </div>
          )}

          {/* Chart + data labels — only when a symbol is selected */}
          {activeSymbol && (
            <SymbolChart
              key={activeSymbol}
              symbol={activeSymbol}
              history={activeHistory}
              latest={activeLatest}
            />
          )}

          {/* Prompt when type selected but no symbol yet */}
          {activeType && !activeSymbol && activeTypeSymbols.length > 0 && (
            <div className="no-data"><p>Select a symbol to view its chart.</p></div>
          )}
        </div>
      )}
    </div>
  );
});

StockVisualizer.displayName = 'StockVisualizer';
export default StockVisualizer;