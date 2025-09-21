package com.trader.app.analytics.providers.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RealStockDataProvider {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.api.key}")
    private String apiKey;
    
    @Value("${stock.api.base-url}")
    private String baseUrl;
    
    public RealStockDataProvider() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    public StockData getRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", 
                                     baseUrl, symbol, apiKey);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode quote = root.get("Global Quote");
            
            if (quote != null) {
                return new StockData(
                    symbol,
                    Double.parseDouble(quote.get("03. high").asText()),
                    Double.parseDouble(quote.get("04. low").asText()),
                    Double.parseDouble(quote.get("02. open").asText()),
                    Double.parseDouble(quote.get("05. price").asText()), // current price as close
                    Long.parseLong(quote.get("06. volume").asText())
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching real stock data for " + symbol + ": " + e.getMessage());
        }
        
        return null;
    }
    
    public StockData getIntradayData(String symbol) {
        try {
            String url = String.format("%s?function=TIME_SERIES_INTRADAY&symbol=%s&interval=1min&apikey=%s", 
                                     baseUrl, symbol, apiKey);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode timeSeries = root.get("Time Series (1min)");
            
            if (timeSeries != null) {
                // Get the latest entry
                String latestTime = timeSeries.fieldNames().next();
                JsonNode latestData = timeSeries.get(latestTime);
                
                return new StockData(
                    symbol,
                    Double.parseDouble(latestData.get("2. high").asText()),
                    Double.parseDouble(latestData.get("3. low").asText()),
                    Double.parseDouble(latestData.get("1. open").asText()),
                    Double.parseDouble(latestData.get("4. close").asText()),
                    Long.parseLong(latestData.get("5. volume").asText())
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching intraday data for " + symbol + ": " + e.getMessage());
        }
        
        return null;
    }
}