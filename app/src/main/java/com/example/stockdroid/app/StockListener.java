package com.example.stockdroid.app;

/**
 * Interface to allow data passing between tasks and fragments.
 *
 * Created by tim on 5/17/14.
 */
public interface StockListener {
    public void onStockLoaded(final String symbol, final String stockData, final String chartData);
}
