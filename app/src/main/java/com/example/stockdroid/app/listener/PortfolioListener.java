package com.example.stockdroid.app.listener;

/**
 * Created by tim on 5/21/14.
 */
public interface PortfolioListener {
    public void onStockAdded(String symbol);
    public void onStockSelected(String symbol);
}
