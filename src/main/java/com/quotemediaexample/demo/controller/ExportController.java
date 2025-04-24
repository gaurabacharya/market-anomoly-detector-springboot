package com.quotemediaexample.demo.controller;

import com.quotemediaexample.demo.service.AnomalyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ExportController {

    private final AnomalyService anomalyService;

    @GetMapping(value = "/anomaly/v1/export/daily", produces = "text/csv")
    public void exportAnomalies(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=anomalies.csv");
        anomalyService.writeCsv(response.getWriter());
    }
}
