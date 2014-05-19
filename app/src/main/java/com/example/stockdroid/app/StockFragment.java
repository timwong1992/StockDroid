package com.example.stockdroid.app;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
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
            // Format: Symbol & Last Trade & Change & Open & Volume & 52 Week Low & 52 Week High (15 min delay)
            final String stockURI = String.format(getString(R.string.stockURI) + "s=%s&f=l1c1ovjkn",
                    symbol);
            // Format: Symbol & Start Month & Start Day & Start Year & End Month & End Day & End Year
            // Months are from 0-11, 0 being January
            final String chartURI = String.format(getString(R.string.chartURI) + "s=%s&a=%d&b=%s&c=%s&d=%d&e=%s&f=%s&n",
                    symbol, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.YEAR));
            new QueryTask(StockFragment.this, symbol, stockURI, chartURI, new MainStockListener()).execute();
        }
    };

    // detect if search edit text is focused on, and display or remove virtual keyboard as required
    private View.OnFocusChangeListener searchOnFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            System.out.println("Focus changed: " + hasFocus);
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
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
        searchEditText.setOnFocusChangeListener(searchOnFocusListener);
        searchButton = (Button) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(searchOnClickListener);
        return rootView;
    }

    /**
     * Animates the search bar to the top upon successfully loading stock data.
     */
    private void moveSearchBar() {
        Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        // weird animation maths
        TranslateAnimation move = new TranslateAnimation(0, 0,
                0, -p.y/2 + getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"))*2.5f);
        move.setDuration(500);
        move.setFillAfter(true);

        searchEditText.startAnimation(move);
        searchButton.startAnimation(move);
    }

    private class MainStockListener implements StockListener {
        @Override
        public void onStockLoaded(String symbol, String stockData, String chartData) {
            moveSearchBar();
            System.out.println(stockData);
            System.out.println(chartData);
        }
    }

}
