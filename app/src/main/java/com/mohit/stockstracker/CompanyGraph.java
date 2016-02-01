package com.mohit.stockstracker;

/**
 * Created by Mohit on 26-04-2015.
 */
public class CompanyGraph {
    String date;
    String open;
    String high;
    String low;
    String close;
    String volume;

    public CompanyGraph(String date, String open, String high, String low, String close, String volume) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}
