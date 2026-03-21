package com.trader.analytics.service;

import com.trader.domain.model.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Reactive multicast sink that replays the last N stock data events.
 * The Kafka consumer pushes into this sink; analysis services pull from it.
 */
@Component
public class StockDataSink {

    private final Sinks.Many<StockData> sink;
    private final Flux<StockData> stream;

    public StockDataSink(@Value("${analytics.sink-buffer-size:200}") int bufferSize) {
        this.sink = Sinks.many().replay().limit(bufferSize);
        this.stream = sink.asFlux();
    }

    public void push(StockData data) {
        sink.tryEmitNext(data);
    }

    public Flux<StockData> stream() {
        return stream;
    }

    public Flux<StockData> streamFor(String symbol) {
        return stream.filter(d -> d.symbol().equalsIgnoreCase(symbol));
    }
}
