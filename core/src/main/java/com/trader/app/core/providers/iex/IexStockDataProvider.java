package com.trader.app.core.providers.iex;

import com.fasterxml.jackson.databind.JsonNode;
import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("iexProvider")
@Slf4j
public class IexStockDataProvider implements StockDataProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final int rateLimit;

    public IexStockDataProvider(WebClient.Builder webClientBuilder, StockProperties properties) {
        StockProperties.ProviderConfig config = properties.providers().get("iex");
        this.webClient = webClientBuilder.baseUrl(config.baseUrl()).build();
        this.apiKey = config.apiKey();
        this.rateLimit = config.rateLimit();
    }

    @Override
    public Mono<StockData> getRealTimeQuote(String symbol) {
        return webClient.get()
                .uri("/stock/{symbol}/quote?token={token}", symbol, apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> new StockData(
                        symbol,
                        root.get("high").asDouble(),         // high
                        root.get("low").asDouble(),          // low
                        root.get("open").asDouble(),         // open
                        root.get("close").asDouble(),        // close — official daily close price
                        root.get("volume").asLong()          // volume
                ))
                .onErrorResume(e -> {
                    log.error("IEX API error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override public String getProviderName() { return "iex"; }
    @Override public int getRateLimit()        { return rateLimit; }
}