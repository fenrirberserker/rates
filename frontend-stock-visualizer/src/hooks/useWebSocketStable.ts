import { useState, useEffect, useRef, useCallback } from 'react';
// Use Server-Sent Events for demo mode
import { StockData, ConnectionStatus } from '../types/stockTypes';

export const useWebSocketStable = () => {
  const [currentData, setCurrentData] = useState<StockData | null>(null);
  const [connectionStatus, setConnectionStatus] = useState<ConnectionStatus>({
    connected: false
  });
  const [stockData, setStockData] = useState<StockData[]>([]);
  const socketRef = useRef<EventSource | null>(null);
  


  const connect = useCallback(() => {
    try {
      // Reset state on new connection
      setStockData([]);
      setCurrentData(null);
      
      const eventSource = new EventSource('http://localhost:8080/api/trading/events');
      socketRef.current = eventSource;
      
      eventSource.onopen = () => {
        console.log('Connected to SSE server');
        setConnectionStatus({
          connected: true,
          lastUpdate: new Date()
        });
      };
      
      eventSource.onmessage = (event) => {
        const rawData = JSON.parse(event.data);
        
        // Filter only AAPL data
        if (rawData.symbol !== 'AAPL') {
          return;
        }
        
        const timestamp = new Date();
        const stockData: StockData = {
          symbol: rawData.symbol,
          high: rawData.high,
          low: rawData.low,
          open: rawData.open,
          close: rawData.close,
          volume: rawData.volume,
          timestamp,
          id: `${timestamp.getTime()}`,
          time: timestamp.toLocaleTimeString('en-US', { 
            hour12: false, 
            hour: '2-digit', 
            minute: '2-digit', 
            second: '2-digit' 
          }),
          spread: rawData.high - rawData.low,
          index: timestamp.getTime()
        };

        setCurrentData(stockData);
        
        // Update state to trigger re-render
        setStockData(prev => {
          const maxPoints = 100;
          const newData = [...prev, stockData];
          return newData.length > maxPoints ? newData.slice(-maxPoints) : newData;
        });
        
        setConnectionStatus(prev => ({
          ...prev,
          lastUpdate: timestamp
        }));
      };
      
      eventSource.onerror = (error) => {
        console.error('SSE connection error:', error);
        setConnectionStatus({
          connected: false,
          error: 'Connection failed'
        });
      };

    } catch (error) {
      console.error('Failed to establish WebSocket connection:', error);
      setConnectionStatus({
        connected: false,
        error: 'Failed to connect'
      });
    }
  }, []);

  const disconnect = useCallback(() => {
    if (socketRef.current) {
      socketRef.current.close();
      socketRef.current = null;
    }
  }, []);

  useEffect(() => {
    connect();

    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  return {
    stockData,
    currentData,
    connectionStatus,
    connect,
    disconnect
  };
};