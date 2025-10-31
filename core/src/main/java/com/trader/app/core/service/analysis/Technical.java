package com.trader.app.core.service.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.BiPredicate;

@Service
public class Technical {

    @Value(value = "${analytics.elementsInTheMiddle}")
    private int intermediateElements; // Number of elements between iterations to consider a tendency

    //Rules
    BiPredicate<Integer, Integer> isIndexDistance = (previous, current) -> (current - previous) > intermediateElements;
    BiPredicate<Map<String, Integer>, Map<String, Integer>> isBullish = (previous, current) -> {
        if(current.get("Low") > previous.get("High") && current.get("High") > current.get("Low")){
            System.out.println("Bullish pattern detected at index " + current);
            return true;
        }
        return false;
    } ;
    BiPredicate<Map<String, Integer>, Map<String, Integer>> isBearish = (previous, current) -> {
        if(current.get("High") < previous.get("Low") && current.get("Low") < current.get("High")){
            System.out.println("Bearish pattern detected at index " + current);
            return true;
        }
        return false;
    } ;



    public String readValues() throws IOException, ExecutionException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Random random = new Random();
        int rand = random.nextInt(2) + 1;
        //ClassPathResource resource = new ClassPathResource("samples/sample-bearish" + rand + ".json");
        ClassPathResource resource = new ClassPathResource("samples/sample-bullish1.json");
        
        List<Map<String, Integer>> data = mapper.readValue(
            resource.getInputStream(), 
            new TypeReference<List<Map<String, Integer>>>() {}
        );

        String analysisResult = "";
        Future<Integer> bullishCount;
        Future<Integer> bearishCount;
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(2,2,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());
        bullishCount = executor.submit(() -> {return analyseBullish(data);});
        bearishCount = executor.submit(() -> {return analyseBearish(data);});
        executor.shutdown();

        System.out.println("Bullish patterns found: " + bullishCount.get());
        System.out.println("Bearish patterns found: " + bearishCount.get());


       if(bullishCount.get() > bearishCount.get()){
           analysisResult = "Overall trend is Bullish";
            System.out.println(analysisResult);
        } else if(bearishCount.get() > bullishCount.get()){
           analysisResult = "Overall trend is Bearish";
            System.out.println(analysisResult);
        } else {
           analysisResult = "Overall trend is Neutral";
            System.out.println(analysisResult);
        }
       return analysisResult;
    }

    public Integer analyseBullish(List<Map<String, Integer>> data){

        int incidences = 0;
        Map<String, Integer> current=null;
        Map<String, Integer> previous=null;
        for (int i = 0; i < data.size(); i++) {
            System.out.println("Thread Bullish->  High: " + data.get(i).get("High") + ", Low: " + data.get(i).get("Low"));
            current = data.get(i);

            if(i>0){
                previous  = previous == null?data.get(i-1):previous;

                if(isBullish.test(previous, current)){
                    previous =current;
                    incidences++;
                };
            }
        }
        return incidences;
    }

    public Integer analyseBearish(List<Map<String, Integer>> data){

        int incidences = 0;
        Map<String, Integer> current=null;
        Map<String, Integer> previous=null;
        for (int i = 0; i < data.size(); i++) {
            System.out.println("Thread Bearish-> High: " + data.get(i).get("High") + ", Low: " + data.get(i).get("Low"));
            current = data.get(i);

            if(i>0){
                previous  = previous == null?data.get(i-1):previous;

                if(isBearish.test(previous, current)){
                    previous =current;
                    incidences++;
                }
            }
        }
        return incidences;
    }
}
