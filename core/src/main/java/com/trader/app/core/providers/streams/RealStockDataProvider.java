package com.trader.app.core.providers.streams;

import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.StockDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Selects the active StockDataProvider at startup based on stock.data.provider config.
 *
 * Uses Spring's List<StockDataProvider> injection — every @Component that implements
 * StockDataProvider is automatically registered here. Adding a new provider requires
 * zero changes to this class (Open/Closed Principle).
 */
@Component
@Slf4j
public class RealStockDataProvider {

    private final StockDataProvider activeProvider;

    public RealStockDataProvider(List<StockDataProvider> providers, StockProperties properties) {
        String providerName = properties.data().provider();

        this.activeProvider = providers.stream()
                .filter(p -> p.getProviderName().equalsIgnoreCase(providerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown provider '" + providerName + "'. " +
                        "Available: " + providers.stream()
                                .map(StockDataProvider::getProviderName)
                                .toList()
                ));

        log.info("Active stock data provider: {} (rate limit: {}/min)",
                activeProvider.getProviderName(), activeProvider.getRateLimit());
    }

    public Mono<StockData> getRealTimeQuote(String symbol) {
        return activeProvider.getRealTimeQuote(symbol);
    }

    public String getProviderName() {
        return activeProvider.getProviderName();
    }

    public int getRateLimit() {
        return activeProvider.getRateLimit();
    }
}