// Stock data interfaces
export interface StockData {
  symbol: string;
  high: number;
  low: number;
  open: number;
  close: number;
  volume: number;
  timestamp: Date;
  id: string;
  time: string;
  spread: number;
  index: number;
}

export interface ConnectionStatus {
  connected: boolean;
  error?: string;
  lastUpdate?: Date;
}
