package com.trader.app.core.providers;

import com.trader.domain.model.StockData;
import reactor.core.publisher.Mono;

public interface StockDataProvider {

    /**
     * Fetches a real-time quote for the given symbol.
     * Returns Mono.empty() if the provider has no data (rate limited, bad symbol, etc.).
     * Errors are handled internally — callers use switchIfEmpty() for fallback.
     */
    Mono<StockData> getRealTimeQuote(String symbol);

    /**
     * Provider identifier — must match the value used in stock.data.provider config.
     */
    String getProviderName();

    /**
     * Maximum requests per minute this provider allows on the free tier.
     */
    int getRateLimit();
}