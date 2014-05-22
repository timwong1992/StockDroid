package com.example.stockdroid.app.stock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tim on 5/20/14.
 */
public class StockData {
    private final String name;
    private final String symbol;
    private final Calendar calendar;
    private final double price;
    private final double open;
    private final double change;
    private final double yearHigh;
    private final double yearLow;
    private final long volume;
    // random memory ramblings
    // 48 bytes for primitive variables
    // 432 bytes for calendar
    // estimate 16 bytes for symbol, 48 bytes for name
    // 3 pointers = 12 bytes

    // each StockData object is 556 bytes
    // up to 250 items = 135.74kb total in device RAM... can this be reduced?

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public StockData(final String name, final String symbol, final Calendar calendar,
                     final double price, final double open, final double change, final double yearLow,
                     final double yearHigh, final long volume) throws ParseException {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.open = open;
        this.volume = volume;
        this.change = change;
        this.yearHigh = yearHigh;
        this.yearLow = yearLow;
        this.calendar = calendar;
    }

    public StockData(final String name, final String symbol, final String date, final double price,
                     final double open, final double change, final double yearLow, final double yearHigh,
                     final long volume) throws ParseException {
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.open = open;
        this.volume = volume;
        this.change = change;
        this.yearHigh = yearHigh;
        this.yearLow = yearLow;
        this.calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        calendar.setTime(formatter.parse(date));
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getChange() {
        return change;
    }

    public double getOpen() {
        return open;
    }

    public String getDate() {
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public double getPrice() {
        return price;
    }

    public double getYearLow() {
        return yearLow;
    }

    public double getYearHigh() {
        return yearHigh;
    }

    public long getVolume() {
        return volume;
    }

}
