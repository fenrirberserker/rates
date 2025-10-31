package com.trader.app.core.providers.iex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("iexProvider")
public class IexStockDataProvider implements StockDataProvider {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.providers.iex.api-key}")
    private String apiKey;
    
    @Value("${stock.providers.iex.base-url}")
    private String baseUrl;
    
    public IexStockDataProvider() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public StockData getRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s/stock/%s/quote?token=%s", baseUrl, symbol, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            return new StockData(
                symbol,
                root.get("high").asDouble(),
                root.get("low").asDouble(),
                root.get("open").asDouble(),
                root.get("latestPrice").asDouble(),
                root.get("volume").asLong()
            );
        } catch (Exception e) {
            System.err.println("IEX API error for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public String getProviderName() {
        return "iex";
    }
    
    @Override
    public int getRateLimit() {
        return 100;
    }
}
