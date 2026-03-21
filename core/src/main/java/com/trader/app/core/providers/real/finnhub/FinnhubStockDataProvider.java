package com.trader.app.core.providers.real.finnhub;

import com.fasterxml.jackson.databind.JsonNode;
import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.domain.model.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("finnhubProvider")
@Slf4j
public class FinnhubStockDataProvider implements StockDataProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final int rateLimit;

    public FinnhubStockDataProvider(WebClient.Builder webClientBuilder, StockProperties properties) {
        StockProperties.ProviderConfig config = properties.providers().get("finnhub");
        this.webClient = webClientBuilder.baseUrl(config.baseUrl()).build();
        this.apiKey = config.apiKey();
        this.rateLimit = config.rateLimit();
    }

    @Override
    public Mono<StockData> getRealTimeQuote(String symbol) {
        return webClient.get()
                .uri("/quote?symbol={symbol}&token={token}", symbol, apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> new StockData(
                        symbol,
                        root.get("h").asDouble(),            // high
                        root.get("l").asDouble(),            // low
                        root.get("o").asDouble(),            // open
                        root.get("c").asDouble(),            // close (current price)
                        root.path("v").asLong(0L)            // volume — not in Finnhub basic /quote; 0 if absent
                ))
                .onErrorResume(e -> {
                    log.error("Finnhub API error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override public String getProviderName() { return "finnhub"; }
    @Override public int getRateLimit()        { return rateLimit; }
}