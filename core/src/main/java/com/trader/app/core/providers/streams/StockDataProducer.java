package com.trader.app.core.providers.streams;

import com.trader.app.config.StockProperties;
import com.trader.domain.model.StockData;
import com.trader.app.core.providers.fake.FakeStockDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class StockDataProducer {

    private final KafkaTemplate<String, StockData> kafkaTemplate;
    private final RealStockDataProvider realDataProvider;
    private final FakeStockDataProvider fakeDataProvider;
    private final StockProperties properties;

    @Value("${kafka.topic.stock-data:stock-data}")
    private String topic;

    public StockDataProducer(KafkaTemplate<String, StockData> kafkaTemplate,
                             RealStockDataProvider realDataProvider,
                             FakeStockDataProvider fakeDataProvider,
                             StockProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.realDataProvider = realDataProvider;
        this.fakeDataProvider = fakeDataProvider;
        this.properties = properties;
    }

    /**
     * Starts the reactive production pipeline and returns a Disposable handle.
     * All timing, symbols, and data ranges come from application.yml — no
     * magic numbers in code.
     */
    public Disposable start() {
        boolean useReal = properties.data().useReal();
        Duration interval = useReal
                ? Duration.ofSeconds(properties.data().interval().realSeconds())
                : Duration.ofSeconds(properties.data().interval().fakeSeconds());

        log.info("Producer starting — mode: {}, interval: {}s, topic: {}, symbols: {}",
                useReal ? "REAL" : "FAKE", interval.getSeconds(), topic,
                properties.data().symbols());

        return buildDataFlux(useReal, interval)
                .flatMap(data -> Mono.fromFuture(() -> kafkaTemplate.send(topic, data.symbol(), data)))
                .doOnNext(r -> log.debug("Kafka <- [{}] {}", useReal ? "REAL" : "FAKE",
                        r.getProducerRecord().value().symbol()))
                .doOnError(e -> log.error("Producer pipeline error", e))
                .retry()
                .subscribe();
    }

    private Flux<StockData> buildDataFlux(boolean useReal, Duration interval) {
        List<String> symbols = properties.data().symbols();

        if (useReal) {
            AtomicInteger index = new AtomicInteger(0);
            return Flux.interval(interval)
                    .map(tick -> symbols.get((int) (index.getAndIncrement() % symbols.size())))
                    .flatMap(symbol ->
                            realDataProvider.getRealTimeQuote(symbol)
                                    .map(data -> data.withType(properties.data().typeOf(symbol)))
                                    .switchIfEmpty(Mono.fromSupplier(() -> {
                                        log.warn("No data returned for {}, using fake", symbol);
                                        return fakeDataProvider.getQuote(symbol);
                                    }))
                    );
        }

        // Emit one data point for every symbol on each tick so every symbol
        // updates at the configured interval, not just one random symbol per tick.
        return Flux.interval(interval)
                .flatMapIterable(tick -> symbols.stream()
                        .map(fakeDataProvider::getQuote)
                        .toList());
    }
}