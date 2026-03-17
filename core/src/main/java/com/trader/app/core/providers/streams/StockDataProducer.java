package com.trader.app.core.providers.streams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class StockDataProducer {

    @Autowired
    private KafkaTemplate<String, StockData> kafkaTemplate;

    @Autowired
    private RealStockDataProvider realDataProvider;

    @Value("${stock.data.use-real:false}")
    private boolean useRealData;

    @Value("${kafka.topic.stock-data:stock-data}")
    private String topic;

    private final Random random;
    private final ScheduledExecutorService scheduler;

    private static final String[] SYMBOLS = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};
    private int symbolIndex = 0;

    public StockDataProducer() {
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startProducing() {
        // Real data: 12s interval to respect ~5 req/min API rate limits
        // Fake data: 1s interval for a smooth demo experience
        int intervalSeconds = useRealData ? 12 : 1;
        System.out.println("Producer starting — mode: " + (useRealData ? "REAL" : "FAKE")
                + ", interval: " + intervalSeconds + "s, topic: " + topic);
        scheduler.scheduleAtFixedRate(this::publishStockData, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    private void publishStockData() {
        try {
            StockData stockData;

            if (useRealData) {
                String symbol = SYMBOLS[symbolIndex % SYMBOLS.length];
                symbolIndex++;
                stockData = realDataProvider.getRealTimeQuote(symbol);
                if (stockData == null) {
                    stockData = generateFakeData(symbol);
                    System.out.println("Real data unavailable for " + symbol + ", using fake");
                }
            } else {
                String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
                stockData = generateFakeData(symbol);
            }

            // Publish to Kafka — key is the stock symbol for partitioning
            kafkaTemplate.send(topic, stockData.symbol(), stockData);
            System.out.println("Kafka -> " + topic + " [" + (useRealData ? "REAL" : "FAKE") + "]: " + stockData);

        } catch (Exception e) {
            System.err.println("Error publishing stock data: " + e.getMessage());
        }
    }

    private StockData generateFakeData(String symbol) {
        double basePrice = 100 + random.nextDouble() * 400;
        return new StockData(
                symbol,
                basePrice + random.nextDouble() * 10,  // high
                basePrice - random.nextDouble() * 10,  // low
                basePrice + random.nextDouble() * 5,   // open
                basePrice + random.nextDouble() * 5,   // close
                (long) (1000 + random.nextInt(10000))  // volume
        );
    }

    public void stop() {
        scheduler.shutdown();
    }
}