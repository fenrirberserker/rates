package com.trader.app.analytics.controller;

import com.trader.app.analytics.service.analysis.Technical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/analyse")
public class AnalysisController {

    @Autowired
    private Technical technical;

    @GetMapping("/technical")
    public String technical() throws IOException, ExecutionException, InterruptedException {
        technical.readValues();
        return "Technical analysis completed - check console output";
    }
}