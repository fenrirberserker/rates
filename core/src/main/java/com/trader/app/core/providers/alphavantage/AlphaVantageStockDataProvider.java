package com.trader.app.core.providers.alphavantage;

import com.fasterxml.jackson.databind.JsonNode;
import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.app.core.providers.streams.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component("alphavantageProvider")
@Slf4j
public class AlphaVantageStockDataProvider implements StockDataProvider {

    private final WebClient webClient;
    private final String apiKey;
    private final int rateLimit;

    public AlphaVantageStockDataProvider(WebClient.Builder webClientBuilder, StockProperties properties) {
        StockProperties.ProviderConfig config = properties.providers().get("alphavantage");
        this.webClient = webClientBuilder.baseUrl(config.baseUrl()).build();
        this.apiKey = config.apiKey();
        this.rateLimit = config.rateLimit();
    }

    @Override
    public Mono<StockData> getRealTimeQuote(String symbol) {
        return webClient.get()
                .uri("?function=GLOBAL_QUOTE&symbol={symbol}&apikey={key}", symbol, apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(root -> {
                    JsonNode quote = root.get("Global Quote");
                    if (quote == null || quote.isEmpty()) {
                        log.warn("AlphaVantage returned empty Global Quote for {}", symbol);
                        return Mono.empty();
                    }
                    return Mono.just(new StockData(
                            symbol,
                            Double.parseDouble(quote.get("03. high").asText()),    // high
                            Double.parseDouble(quote.get("04. low").asText()),     // low
                            Double.parseDouble(quote.get("02. open").asText()),    // open
                            Double.parseDouble(quote.get("05. price").asText()),   // close (AlphaVantage field name is "05. price")
                            Long.parseLong(quote.get("06. volume").asText())       // volume
                    ));
                })
                .onErrorResume(e -> {
                    log.error("AlphaVantage API error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

    @Override public String getProviderName() { return "alphavantage"; }
    @Override public int getRateLimit()        { return rateLimit; }
}