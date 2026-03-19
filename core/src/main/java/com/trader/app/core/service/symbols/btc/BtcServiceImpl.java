package com.trader.app.core.service.symbols.btc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class BtcServiceImpl implements BtcService {

    private final WebClient webClient;

    public BtcServiceImpl(WebClient.Builder webClientBuilder,
                          @Value("${btc.provider.url}") String providerUrl) {
        this.webClient = webClientBuilder.baseUrl(providerUrl).build();
    }

    @Override
    public Mono<String> getBTC(String currency, Integer value) {
        return webClient.get()
                .uri("?currency={currency}&value={value}", currency, value)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("BTC conversion response: {}", response))
                .onErrorResume(e -> {
                    log.error("blockchain.info error: {}", e.getMessage());
                    return Mono.empty();
                });
    }
}