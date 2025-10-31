package com.trader.app.core.controller;

import com.trader.app.core.service.analysis.Technical;
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
        return technical.readValues();
    }
}
