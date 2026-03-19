import { useState, useEffect, useRef, useCallback } from 'react';
import { StockData, ConnectionStatus } from '../types/stockTypes';
import { config } from '../config';

export const useWebSocketStable = () => {
  const [historyBySymbol, setHistoryBySymbol] = useState<Record<string, StockData[]>>({});
  const [latestBySymbol, setLatestBySymbol]   = useState<Record<string, StockData>>({});
  const [connectionStatus, setConnectionStatus] = useState<ConnectionStatus>({ connected: false });
  const socketRef = useRef<EventSource | null>(null);

  const connect = useCallback(() => {
    const eventSource = new EventSource(config.sseUrl);
    socketRef.current = eventSource;

    eventSource.onopen = () => {
      console.log('Connected to SSE server');
      setConnectionStatus({ connected: true, lastUpdate: new Date() });
    };

    const handleStockEvent = (event: MessageEvent) => {
      const rawData   = JSON.parse(event.data);
      const timestamp = new Date();

      const stockData: StockData = {
        symbol:    rawData.symbol,
        type:      rawData.type ?? 'OTHER',
        high:      rawData.high,
        low:       rawData.low,
        open:      rawData.open,
        close:     rawData.close,
        volume:    rawData.volume,
        timestamp,
        id:        `${rawData.symbol}-${timestamp.getTime()}`,
        time:      timestamp.toLocaleTimeString('en-US', {
          hour12: false, hour: '2-digit', minute: '2-digit', second: '2-digit'
        }),
        spread:    rawData.high - rawData.low,
        index:     timestamp.getTime()
      };

      // Append to per-symbol history, capped at config.maxPointsPerSymbol
      setHistoryBySymbol(prev => {
        const existing = prev[stockData.symbol] ?? [];
        const updated  = [...existing, stockData];
        return {
          ...prev,
          [stockData.symbol]: updated.length > config.maxPointsPerSymbol
            ? updated.slice(-config.maxPointsPerSymbol)
            : updated
        };
      });

      // Track latest value per symbol for the data-label strip
      setLatestBySymbol(prev => ({ ...prev, [stockData.symbol]: stockData }));

      setConnectionStatus(prev => ({ ...prev, lastUpdate: timestamp }));
    };

    // Named SSE events require addEventListener — onmessage only fires for unnamed events
    eventSource.addEventListener(config.sseEventType, handleStockEvent);

    eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      setConnectionStatus({ connected: false, error: 'Connection failed' });
    };
  }, []);

  const disconnect = useCallback(() => {
    if (socketRef.current) {
      socketRef.current.close();
      socketRef.current = null;
    }
  }, []);

  useEffect(() => {
    connect();
    return () => { disconnect(); };
  }, [connect, disconnect]);

  return { historyBySymbol, latestBySymbol, connectionStatus };
};