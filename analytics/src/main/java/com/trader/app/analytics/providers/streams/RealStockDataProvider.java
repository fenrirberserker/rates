package com.trader.app.analytics.providers.streams;

import com.trader.app.analytics.providers.StockDataProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RealStockDataProvider {
    
    private final StockDataProvider stockDataProvider;
    
    public RealStockDataProvider(@Value("${stock.data.provider}") String providerName,
                                @Qualifier("finnhubProvider") StockDataProvider finnhubProvider,
                                @Qualifier("alphavantageProvider") StockDataProvider alphavantageProvider,
                                @Qualifier("iexProvider") StockDataProvider iexProvider,
                                @Qualifier("twelvedataProvider") StockDataProvider twelvedataProvider) {
        
        switch (providerName.toLowerCase()) {
            case "finnhub":
                this.stockDataProvider = finnhubProvider;
                break;
            case "alphavantage":
                this.stockDataProvider = alphavantageProvider;
                break;
            case "iex":
                this.stockDataProvider = iexProvider;
                break;
            case "twelvedata":
                this.stockDataProvider = twelvedataProvider;
                break;
            default:
                throw new IllegalArgumentException("Unsupported provider: " + providerName);
        }
        
        System.out.println("Using stock data provider: " + this.stockDataProvider.getProviderName() + 
                          " (Rate limit: " + this.stockDataProvider.getRateLimit() + " req/min)");
    }
    
    public StockData getRealTimeQuote(String symbol) {
        return stockDataProvider.getRealTimeQuote(symbol);
    }
    
    public String getProviderName() {
        return stockDataProvider.getProviderName();
    }
    
    public int getRateLimit() {
        return stockDataProvider.getRateLimit();
    }
    

}