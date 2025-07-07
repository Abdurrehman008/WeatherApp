package com.example.weatherapp;

public class ForecastItem {
    public String date;
    public String temp;
    public String condition;

    public ForecastItem(String date, String temp, String condition) {
        this.date = date;
        this.temp = temp;
        this.condition = condition;
    }
}