package com.trader.app.core.service.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final BiPredicate<Map<String, Integer>, Map<String, Integer>> isBullish =
            (previous, current) -> current.get("Low") > previous.get("High")
                    && current.get("High") > current.get("Low");

    private final BiPredicate<Map<String, Integer>, Map<String, Integer>> isBearish =
            (previous, current) -> current.get("High") < previous.get("Low")
                    && current.get("Low") < current.get("High");

    /**
     * Loads sample data and runs bullish/bearish analysis concurrently.
     *
     * Mono.zip() subscribes to both Monos simultaneously — each runs on its own
     * boundedElastic thread. The result is available when both complete, without
     * blocking the Netty event loop at any point.
     *
     * Previously this used a ThreadPoolExecutor created per-request (expensive)
     * and blocked on Future.get() (blocking the calling thread).
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
                .map(tuple -> {
                    int bullishCount = tuple.getT1();
                    int bearishCount = tuple.getT2();
                    log.info("Analysis complete — bullish: {}, bearish: {}", bullishCount, bearishCount);
                    if (bullishCount > bearishCount) return "Overall trend is Bullish";
                    if (bearishCount > bullishCount) return "Overall trend is Bearish";
                    return "Overall trend is Neutral";
                });
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