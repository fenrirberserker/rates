package com.trader.app.analytics.service.analysis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class Technical {

    public void readValues() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("sample-bearish.json");
        
        List<Map<String, Integer>> data = mapper.readValue(
            resource.getInputStream(), 
            new TypeReference<List<Map<String, Integer>>>() {}
        );
        
        for (Map<String, Integer> item : data) {
            System.out.println("High: " + item.get("high") + ", Low: " + item.get("low"));
        }
    }
}
