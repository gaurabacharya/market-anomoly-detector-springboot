package com.quotemediaexample.demo.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.RequiredArgsConstructor;
import com.quotemediaexample.demo.service.AnomalyService;
import com.quotemediaexample.demo.service.SymbolState;
import java.util.Map;

@RestController
@RequestMapping("/anomaly/v1")
@RequiredArgsConstructor
public class StatusController {

  private final AnomalyService svc;

    @GetMapping("/status/{symbol}")
    public Map<String, String> status(@PathVariable String symbol) {
        SymbolState state = svc.latestState(symbol);
        return Map.of(
            "symbol", symbol,
            "status", state.getCurrentState().name()
        );
}
}
