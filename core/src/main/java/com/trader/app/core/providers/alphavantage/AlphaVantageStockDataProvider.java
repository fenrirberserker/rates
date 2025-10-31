package com.trader.app.core.providers.alphavantage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component("alphavantageProvider")
public class AlphaVantageStockDataProvider implements StockDataProvider {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${stock.providers.alphavantage.api-key}")
    private String apiKey;
    
    @Value("${stock.providers.alphavantage.base-url}")
    private String baseUrl;
    
    public AlphaVantageStockDataProvider() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public StockData getRealTimeQuote(String symbol) {
        try {
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s", baseUrl, symbol, apiKey);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode quote = root.get("Global Quote");
            
            if (quote != null) {
                return new StockData(
                    symbol,
                    Double.parseDouble(quote.get("03. high").asText()),
                    Double.parseDouble(quote.get("04. low").asText()),
                    Double.parseDouble(quote.get("02. open").asText()),
                    Double.parseDouble(quote.get("05. price").asText()),
                    Long.parseLong(quote.get("06. volume").asText())
                );
            }
        } catch (Exception e) {
            System.err.println("Alpha Vantage API error for " + symbol + ": " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public String getProviderName() {
        return "alphavantage";
    }
    
    @Override
    public int getRateLimit() {
        return 5;
    }
}
