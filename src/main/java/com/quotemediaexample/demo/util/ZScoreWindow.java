package com.quotemediaexample.demo.util;

import java.util.Deque;
import java.util.ArrayDeque;

public class ZScoreWindow {
  private final int size;
  private final Deque<Double> window = new ArrayDeque<>();
  private double sum=0, sumSq=0;

  public ZScoreWindow(int size) { this.size = size; }

  public double add(double x) {
    window.addLast(x);
    sum += x; sumSq += x*x;
    if (window.size() > size) {
      double old = window.removeFirst();
      sum -= old; sumSq -= old*old;
    }
    double mean = sum / window.size();
    double std  = Math.sqrt((sumSq/window.size()) - mean*mean);
    return std == 0 ? 0 : (x - mean) / std;      // z‑score
  }
}
