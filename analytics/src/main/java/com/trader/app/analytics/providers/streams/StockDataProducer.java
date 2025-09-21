package com.trader.app.analytics.providers.streams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class StockDataProducer {
    
    @Autowired
    private InMemoryStockQueue queue;
    
    @Autowired
    private RealStockDataProvider realDataProvider;
    
    @Value("${stock.data.use-real:false}")
    private boolean useRealData;
    
    private final Random random;
    private final ScheduledExecutorService scheduler;
    
    private static final String[] SYMBOLS = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN"};
    private int symbolIndex = 0;

    public StockDataProducer() {
        this.random = new Random();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startProducing() {
        // Use longer interval for real data to respect API limits
        int interval = useRealData ? 12 : 1; // 12 seconds for real data (5 calls per minute limit)
        scheduler.scheduleAtFixedRate(this::sendStockData, 0, interval, TimeUnit.SECONDS);
    }

    private void sendStockData() {
        try {
            StockData stockData;
            
            if (useRealData) {
                String symbol = SYMBOLS[symbolIndex % SYMBOLS.length];
                symbolIndex++;
                
                stockData = realDataProvider.getRealTimeQuote(symbol);
                
                // Fallback to fake data if real data fails
                if (stockData == null) {
                    stockData = generateFakeData(symbol);
                    System.out.println("Using fake data for " + symbol);
                }
            } else {
                String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
                stockData = generateFakeData(symbol);
            }

            queue.publish(stockData);
            System.out.println("Sent (" + (useRealData ? "REAL" : "FAKE") + "): " + stockData);
            
        } catch (Exception e) {
            System.err.println("Error producing stock data: " + e.getMessage());
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