package com.mohit.stockstracker;

/**
 * Created by Mohit on 21-04-2015.
 */
public class Company {

    String ticker;
    String name;
    String date;
    String price;
    String percentageChange;
    String valueChange;
    String evaluation;
    String volume;
    String annualHigh;
    String annualLow;
    String high;
    String low;
    String open;


    public Company(String ticker, String name, String price, String percentageChange, String valueChange, String evaluation,String date) {
        this.evaluation = evaluation;
        this.ticker = ticker;
        this.name = name;
        this.percentageChange  = percentageChange;
        this.price = price;
        this.valueChange = valueChange;
        this.date=date;
    }
}
