package com.example.stockdroid.app.listener;

import com.example.stockdroid.app.stock.StockData;

import java.util.List;

/**
 * Interface to allow data passing between tasks and fragments.
 * <p/>
 * Created by tim on 5/17/14.
 */
public interface StockListener {
    public void onStockLoaded(final List<StockData> stocksData);
}
