package com.trader.app.core.service;

import com.trader.app.core.providers.streams.StockDataProducer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class StreamingService {

    @Autowired
    private StockDataProducer producer;

    @Autowired
    private Environment env;

    @Value("${stock.data.use-real:false}")
    private boolean useRealData;

    @Value("${stock.data.provider:finnhub}")
    private String providerName;

    /**
     * Validates config and starts the producer on application startup.
     * The Kafka consumer (@KafkaListener) lifecycle is managed by Spring automatically.
     *
     * To switch between fake and real data set in application.yml:
     *   stock.data.use-real: false  → fake data every 1s  (default, no API key needed)
     *   stock.data.use-real: true   → real API data every 12s (set provider API key)
     */
    @PostConstruct
    public void startStreaming() {
        if (useRealData) {
            String apiKey = env.getProperty("stock.providers." + providerName + ".api-key", "");
            if (apiKey.isBlank()) {
                throw new IllegalStateException(
                    "stock.data.use-real=true but no API key configured for provider '" + providerName + "'. " +
                    "Set the " + providerName.toUpperCase() + "_API_KEY environment variable, " +
                    "or switch back to fake data with stock.data.use-real=false."
                );
            }
        }

        System.out.println("=== Streaming service starting — data mode: "
                + (useRealData ? "REAL (" + providerName + ")" : "FAKE") + " ===");
        producer.startProducing();
    }

    @PreDestroy
    public void stopStreaming() {
        System.out.println("Stopping streaming service...");
        producer.stop();
    }
}