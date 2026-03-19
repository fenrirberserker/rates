package com.trader.app.core.service;

import com.trader.app.config.StockProperties;
import com.trader.app.core.providers.streams.StockData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Reactive multicast sink for StockData events.
 *
 * Buffer size is read from stock.data.sink-buffer-size in application.yml.
 * When a slow SSE client can't keep up, up to that many events are buffered
 * before the subscriber is dropped — protecting the producer from back-pressure.
 */
@Component
@Slf4j
public class StockDataSink {

    private final Sinks.Many<StockData> sink;

    public StockDataSink(StockProperties properties) {
        this.sink = Sinks.many()
                .multicast()
                .onBackpressureBuffer(properties.data().sinkBufferSize());
    }

    public void emit(StockData data) {
        Sinks.EmitResult result = sink.tryEmitNext(data);
        if (result.isFailure()) {
            log.warn("Failed to emit stock data for {}: {}", data.symbol(), result);
        }
    }

    public Flux<StockData> stream() {
        return sink.asFlux();
    }
}