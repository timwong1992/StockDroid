package com.example.stockdroid.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

/**
 * THe main fragment to view stock information.
 *
 * Created by tim on 5/13/14.
 */
public class StockFragment extends Fragment {

    // GUI elements
    private EditText searchEditText;
    private Button searchButton;

    // OnClickListener to detect when query is launched. On default, gathers one week's worth of data.
    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_YEAR, -1);

            final String symbol = searchEditText.getText().toString();
            if (symbol.equals("") || symbol == null) {
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noSymbolError), 3000).show();
                return;
            }
            // Format: Symbol & Last Trade (real time) & Change (real time) & Open & Volume & 52 Week Low & 52 Week High
            final String stockURI = String.format(getString(R.string.stockURI) + "s=%s&f=l1c6ovjkn",
                    symbol);
            // Format: Symbol & Start Month & Start Day & Start Year & End Month & End Day & End Year
            // Months are from 0-11, 0 being January
            final String chartURI = String.format(getString(R.string.chartURI) + "s=%s&a=%d&b=%s&c=%s&d=%d&e=%s&f=%s&n",
                    symbol, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.YEAR));
            new QueryTask(StockFragment.this, symbol, stockURI, chartURI, new MainStockListener()).execute();
        }
    };

    /**
     * Constructor
     */
    public StockFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock, container, false);
        searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);
        searchEditText.setHint(getString(R.string.defaultText));
        searchButton = (Button) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(searchOnClickListener);
        return rootView;
    }


    private class MainStockListener implements StockListener {
        @Override
        public void onStockLoaded(String symbol, String stockData, String chartData) {
            System.out.println(stockData);
            System.out.println(chartData);
        }
    }


}
