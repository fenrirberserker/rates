package com.trader.app.core.providers.twelvedata;

import com.fasterxml.jackson.databind.JsonNode;
import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("twelvedataProvider")
@Slf4j
public class TwelveDataStockDataProvider implements StockDataProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final int rateLimit;

    public TwelveDataStockDataProvider(WebClient.Builder webClientBuilder, StockProperties properties) {
        StockProperties.ProviderConfig config = properties.providers().get("twelvedata");
        this.webClient = webClientBuilder.baseUrl(config.baseUrl()).build();
        this.apiKey = config.apiKey();
        this.rateLimit = config.rateLimit();
    }

    @Override
    public Mono<StockData> getRealTimeQuote(String symbol) {
        return webClient.get()
                .uri("/quote?symbol={symbol}&apikey={key}", symbol, apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> new StockData(
                        symbol,
                        Double.parseDouble(root.get("high").asText()),    // high
                        Double.parseDouble(root.get("low").asText()),     // low
                        Double.parseDouble(root.get("open").asText()),    // open
                        Double.parseDouble(root.get("close").asText()),   // close
                        Long.parseLong(root.get("volume").asText())       // volume
                ))
                .onErrorResume(e -> {
                    log.error("TwelveData API error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override public String getProviderName() { return "twelvedata"; }
    @Override public int getRateLimit()        { return rateLimit; }
}