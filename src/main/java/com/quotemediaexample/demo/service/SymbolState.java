package com.quotemediaexample.demo.service;
import com.quotemediaexample.demo.model.AnomalyState;

public class SymbolState {
    private double lastZScore = 0.0;
    private String name;
    private AnomalyState currentState = AnomalyState.NORMAL;

    public SymbolState(String symbol) {
        name = symbol;
    }

    public void update(double zScore) {
        this.lastZScore = zScore;
        if (Math.abs(zScore) > 3.0) {
            currentState = AnomalyState.CONFIRMED;
        } else if (Math.abs(zScore) > 2.0) {
            currentState = AnomalyState.SUSPECT;
        } else {
            currentState = AnomalyState.NORMAL;
        }
    }

    public AnomalyState getCurrentState() {
        return currentState;
    }

    public double getLastZScore() {
        return lastZScore;
    }

    public String name() {
        return name;
    }


}
