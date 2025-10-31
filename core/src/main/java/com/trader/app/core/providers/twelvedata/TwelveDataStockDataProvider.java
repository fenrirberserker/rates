package com.trader.app.core.providers.twelvedata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("twelvedataProvider")
public class TwelveDataStockDataProvider implements StockDataProvider {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.providers.twelvedata.api-key}")
    private String apiKey;
    
    @Value("${stock.providers.twelvedata.base-url}")
    private String baseUrl;
    
    public TwelveDataStockDataProvider() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public StockData getRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s/quote?symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            
            return new StockData(
                symbol,
                Double.parseDouble(root.get("high").asText()),
                Double.parseDouble(root.get("low").asText()),
                Double.parseDouble(root.get("open").asText()),
                Double.parseDouble(root.get("close").asText()),
                Long.parseLong(root.get("volume").asText())
            );
        } catch (Exception e) {
            System.err.println("Twelve Data API error for " + symbol + ": " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public String getProviderName() {
        return "twelvedata";
    }
    
    @Override
    public int getRateLimit() {
        return 33;
    }
}
