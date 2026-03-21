package com.trader.analytics.service.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trader.domain.model.StockData;
import com.trader.analytics.service.StockDataSink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@Service
@Slf4j
public class Technical {

    @Value("${analytics.elements-in-the-middle}")
    private int intermediateElements;

    @Value("${analytics.sample-file}")
    private String sampleFile;

    private final StockDataSink stockDataSink;

    private final BiPredicate<Map<String, Integer>, Map<String, Integer>> isBullish =
            (previous, current) -> current.get("Low") > previous.get("High")
                    && current.get("High") > current.get("Low");

    private final BiPredicate<Map<String, Integer>, Map<String, Integer>> isBearish =
            (previous, current) -> current.get("High") < previous.get("Low")
                    && current.get("Low") < current.get("High");

    public Technical(StockDataSink stockDataSink) {
        this.stockDataSink = stockDataSink;
    }

    /**
     * Sample-based analysis — loads static JSON and runs concurrent bullish/bearish detection.
     * Both analyses run on separate boundedElastic threads via Mono.zip().
     */
    public Mono<String> readValues() {
        return Mono.fromCallable(this::loadSampleData)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(data -> {
                    Mono<Integer> bullish = Mono.fromCallable(() -> analyseBullish(data))
                            .subscribeOn(Schedulers.boundedElastic());
                    Mono<Integer> bearish = Mono.fromCallable(() -> analyseBearish(data))
                            .subscribeOn(Schedulers.boundedElastic());
                    return Mono.zip(bullish, bearish);
                })
                .map(tuple -> formatResult(tuple.getT1(), tuple.getT2()));
    }

    /**
     * Live analysis — pulls the last N buffered Kafka events for the given symbol
     * and runs the same bullish/bearish detection on real OHLC values.
     */
    public Mono<String> analyzeLive(String symbol) {
        return stockDataSink.streamFor(symbol)
                .take(intermediateElements)
                .collectList()
                .flatMap(dataPoints -> {
                    if (dataPoints.isEmpty()) {
                        return Mono.just("No data received yet for " + symbol.toUpperCase());
                    }
                    List<Map<String, Integer>> mapped = dataPoints.stream()
                            .map(this::toMap)
                            .toList();
                    Mono<Integer> bullish = Mono.fromCallable(() -> analyseBullish(mapped))
                            .subscribeOn(Schedulers.boundedElastic());
                    Mono<Integer> bearish = Mono.fromCallable(() -> analyseBearish(mapped))
                            .subscribeOn(Schedulers.boundedElastic());
                    return Mono.zip(bullish, bearish)
                            .map(t -> "[" + symbol.toUpperCase() + "] " + formatResult(t.getT1(), t.getT2()));
                });
    }

    private Map<String, Integer> toMap(StockData d) {
        return Map.of(
                "High",  d.high()  != null ? d.high().intValue()  : 0,
                "Low",   d.low()   != null ? d.low().intValue()   : 0,
                "Open",  d.open()  != null ? d.open().intValue()  : 0,
                "Close", d.close() != null ? d.close().intValue() : 0
        );
    }

    private String formatResult(int bullishCount, int bearishCount) {
        log.info("Analysis complete — bullish: {}, bearish: {}", bullishCount, bearishCount);
        if (bullishCount > bearishCount) return "Overall trend is Bullish";
        if (bearishCount > bullishCount) return "Overall trend is Bearish";
        return "Overall trend is Neutral";
    }

    private List<Map<String, Integer>> loadSampleData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(sampleFile);
        return mapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<Map<String, Integer>>>() {}
        );
    }

    private Integer analyseBullish(List<Map<String, Integer>> data) {
        int incidences = 0;
        Map<String, Integer> previous = null;
        for (Map<String, Integer> current : data) {
            log.trace("Bullish thread — High: {}, Low: {}", current.get("High"), current.get("Low"));
            if (previous != null && isBullish.test(previous, current)) {
                previous = current;
                incidences++;
            } else if (previous == null) {
                previous = current;
            }
        }
        return incidences;
    }

    private Integer analyseBearish(List<Map<String, Integer>> data) {
        int incidences = 0;
        Map<String, Integer> previous = null;
        for (Map<String, Integer> current : data) {
            log.trace("Bearish thread — High: {}, Low: {}", current.get("High"), current.get("Low"));
            if (previous != null && isBearish.test(previous, current)) {
                previous = current;
                incidences++;
            } else if (previous == null) {
                previous = current;
            }
        }
        return incidences;
    }
}
