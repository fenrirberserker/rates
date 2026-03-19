package com.trader.app.core.service;

import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.streams.StockDataProducer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;

@Service
@Slf4j
public class StreamingService {

    private final StockDataProducer producer;
    private final StockProperties properties;

    private Disposable subscription;

    public StreamingService(StockDataProducer producer, StockProperties properties) {
        this.producer = producer;
        this.properties = properties;
    }

    /**
     * Validates config and starts the reactive producer pipeline on startup.
     * The @KafkaListener in StockKafkaConsumer is managed by Spring automatically.
     */
    @PostConstruct
    public void startStreaming() {
        StockProperties.DataConfig data = properties.data();

        if (data.useReal()) {
            StockProperties.ProviderConfig providerConfig = properties.providers().get(data.provider());
            if (providerConfig == null || providerConfig.apiKey().isBlank()) {
                throw new IllegalStateException(
                        "stock.data.use-real=true but no API key configured for provider '" + data.provider() + "'. " +
                        "Set the " + data.provider().toUpperCase() + "_API_KEY environment variable, " +
                        "or switch back to fake data with stock.data.use-real=false."
                );
            }
        }

        log.info("=== Streaming service starting — data mode: {} ===",
                data.useReal() ? "REAL (" + data.provider() + ")" : "FAKE");
        subscription = producer.start();
    }

    @PreDestroy
    public void stopStreaming() {
        log.info("Stopping streaming service...");
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}