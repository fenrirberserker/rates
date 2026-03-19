package com.trader.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Typesafe binding for all stock.* properties in application.yml.
 *
 * Spring relaxed binding handles kebab-case → camelCase automatically:
 *   use-real         → useReal
 *   symbol-groups    → symbolGroups
 *   base-price-min   → basePriceMin
 *   sink-buffer-size → sinkBufferSize
 *   etc.
 */
@ConfigurationProperties(prefix = "stock")
public record StockProperties(
        DataConfig data,
        Map<String, ProviderConfig> providers
) {
    public record DataConfig(
            boolean useReal,
            String provider,
            Map<String, List<String>> symbolGroups,
            IntervalConfig interval,
            FakeDataConfig fake,
            int sinkBufferSize
    ) {
        /** Flat list of all symbols across all groups — used by the producer. */
        public List<String> symbols() {
            return symbolGroups.values().stream()
                    .flatMap(List::stream)
                    .toList();
        }

        /** Looks up the type for a given symbol; returns "OTHER" if not configured. */
        public String typeOf(String symbol) {
            return symbolGroups.entrySet().stream()
                    .filter(e -> e.getValue().contains(symbol.toUpperCase()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("OTHER");
        }
    }

    /** Polling intervals for fake and real data modes. */
    public record IntervalConfig(
            int realSeconds,
            int fakeSeconds
    ) {}

    /**
     * Parameters for the fake data generator.
     * Final price = basePriceMin + rand(basePriceRange)
     * High  = base + rand(priceVariation)
     * Low   = base - rand(priceVariation)
     * Open  = base + rand(ohlcVariation)
     * Close = base + rand(ohlcVariation)
     * Volume = volumeMin + rand(volumeRange)
     */
    public record FakeDataConfig(
            double basePriceMin,
            double basePriceRange,
            double priceVariation,
            double ohlcVariation,
            long volumeMin,
            long volumeRange
    ) {}

    public record ProviderConfig(
            String apiKey,
            String baseUrl,
            int rateLimit
    ) {}
}