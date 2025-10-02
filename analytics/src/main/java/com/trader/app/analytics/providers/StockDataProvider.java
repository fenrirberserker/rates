package com.trader.app.analytics.providers;

import com.trader.app.analytics.providers.streams.StockData;

public interface StockDataProvider {
    
    /**
     * Get real-time stock quote for a symbol
     * @param symbol Stock symbol (e.g., AAPL, GOOGL)
     * @return StockData or null if error
     */
    StockData getRealTimeQuote(String symbol);
    
    /**
     * Get provider name
     * @return Provider identifier
     */
    String getProviderName();
    
    /**
     * Get rate limit per minute
     * @return Maximum requests per minute
     */
    int getRateLimit();
}