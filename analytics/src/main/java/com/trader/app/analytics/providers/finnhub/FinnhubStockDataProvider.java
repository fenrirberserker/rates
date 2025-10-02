package com.trader.app.analytics.providers.finnhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trader.app.analytics.providers.StockDataProvider;
import com.trader.app.analytics.providers.streams.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("finnhubProvider")
public class FinnhubStockDataProvider implements StockDataProvider {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.providers.finnhub.api-key}")
    private String apiKey;
    
    @Value("${stock.providers.finnhub.base-url}")
    private String baseUrl;
    
    public FinnhubStockDataProvider() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public StockData getRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s/quote?symbol=%s&token=%s", baseUrl, symbol, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            return new StockData(
                symbol,
                root.get("h").asDouble(),  // high
                root.get("l").asDouble(),  // low
                root.get("o").asDouble(),  // open
                root.get("c").asDouble(),  // current/close
                0L  // volume not in basic quote
            );
        } catch (Exception e) {
            System.err.println("Finnhub API error for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public String getProviderName() {
        return "finnhub";
    }
    
    @Override
    public int getRateLimit() {
        return 60;
    }
}