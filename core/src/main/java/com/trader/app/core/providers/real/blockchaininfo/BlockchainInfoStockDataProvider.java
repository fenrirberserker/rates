package com.trader.app.core.providers.real.blockchaininfo;

import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import com.trader.domain.model.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * blockchain.info provider.
 *
 * Conversion endpoint: /to{symbol}?currency={currency}&value={value}
 * Example: /tobtc?currency=USD&value=1  → returns amount of BTC you get for 1 USD
 *
 * getRealTimeQuote delegates to convert() with default USD/1 and inverts the rate
 * to express the price as "1 symbol in USD", consistent with other providers.
 */
@Component("blockchainInfoProvider")
@Slf4j
public class BlockchainInfoStockDataProvider implements StockDataProvider {

    private final WebClient webClient;
    private final int rateLimit;

    public BlockchainInfoStockDataProvider(WebClient.Builder webClientBuilder,
                                           StockProperties properties) {
        StockProperties.ProviderConfig config = properties.providers().get("blockchaininfo");
        this.webClient = webClientBuilder.baseUrl(config.baseUrl()).build();
        this.rateLimit = config.rateLimit();
    }

    /**
     * Converts a fiat {@code value} in {@code currency} to the given {@code symbol}.
     * Returns the raw decimal string from blockchain.info (e.g. "0.000023456").
     */
    public Mono<String> convert(String symbol, String currency, Integer value) {
        return webClient.get()
                .uri("/to{symbol}?currency={currency}&value={value}",
                        symbol.toLowerCase(), currency, value)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(rate -> log.info("blockchain.info {}/{} → {}", symbol, currency, rate))
                .onErrorResume(e -> {
                    log.error("blockchain.info error for {}: {}", symbol, e.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Fetches the current price of {@code symbol} in USD by calling
     * /to{symbol}?currency=USD&value=1 and inverting the rate.
     */
    @Override
    public Mono<StockData> getRealTimeQuote(String symbol) {
        return convert(symbol, "USD", 1)
                .map(rate -> {
                    double price = 1.0 / Double.parseDouble(rate);
                    return new StockData(symbol, price, price, price, price, 0L);
                });
    }

    @Override public String getProviderName() { return "blockchaininfo"; }
    @Override public int getRateLimit()        { return rateLimit; }
}
