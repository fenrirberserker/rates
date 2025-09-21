package com.trader.app.analytics.providers.streams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class StockDataConsumer {
    
    @Autowired
    private InMemoryStockQueue queue;
    
    private final ExecutorService executor;
    private volatile boolean running = false;

    public StockDataConsumer() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void startConsuming() {
        running = true;
        
        executor.submit(() -> {
            while (running) {
                try {
                    StockData stockData = queue.consume();
                    processStockData(stockData);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error consuming messages: " + e.getMessage());
                }
            }
        });
    }

    private void processStockData(StockData stockData) {
        try {
            System.out.println("Received: " + stockData);
            
            // Process the stock data (analyze patterns, store, etc.)
            analyzeStockData(stockData);
            
        } catch (Exception e) {
            System.err.println("Error processing stock data: " + e.getMessage());
        }
    }

    private void analyzeStockData(StockData stockData) {
        // Basic analysis logic
        if (stockData.getHigh() > stockData.getLow() * 1.05) {
            System.out.println("High volatility detected for " + stockData.getSymbol());
        }
        
        if (stockData.getClose() > stockData.getOpen()) {
            System.out.println("Bullish movement for " + stockData.getSymbol());
        } else {
            System.out.println("Bearish movement for " + stockData.getSymbol());
        }
    }

    public void stop() {
        running = false;
        executor.shutdown();
    }
}