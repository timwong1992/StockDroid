package com.example.stockdroid.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.listener.StockListener;
import com.example.stockdroid.app.stock.StockData;
import com.example.stockdroid.app.task.QueryTask;
import com.example.stockdroid.app.view.SearchWidget;
import com.example.stockdroid.app.view.StockView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * The main fragment to view stock information.
 * <p/>
 * Created by tim on 5/13/14.
 */
public class StockFragment extends Fragment {

    // GUI elements
    private RelativeLayout layout;
    private StockView stockDataView;
    private SearchWidget searchWidget;
    private boolean hasSearched;

    private static final String TAG = "StockFragment.java";
    public static final String ASSET_PATH = "file:///android_asset/";

    // OnClickListener to detect when query is launched. On default, gathers one week's worth of data.
    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            query(searchWidget.getSearchEditText().getText().toString());
        }
    };

    // detect if search edit text is focused on, and display or remove virtual keyboard as required
    private View.OnFocusChangeListener searchOnFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            System.out.println("Focus changed: " + hasFocus);
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (hasFocus) {
                imm.showSoftInput(searchWidget.getSearchEditText(), InputMethodManager.SHOW_IMPLICIT);
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

    /**
     * Creates a layoutparams object with wrap_content on default.
     *
     * @return new layoutparams object
     */
    private RelativeLayout.LayoutParams createLayoutParams() {
        return new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock, container, false);
        layout = (RelativeLayout) rootView.findViewById(R.id.mainLayout);

        //View searchWidgetView = inflater.inflate(R.layout.search_widget, layout, true);

        searchWidget = new SearchWidget(rootView.getContext(), null);
        searchWidget.getSearchButton().setOnClickListener(searchOnClickListener);
        searchWidget.getSearchEditText().setFocusable(true);
        searchWidget.getLayout().setFocusableInTouchMode(true);
        searchWidget.getSearchButton().setClickable(true);

        RelativeLayout.LayoutParams params = createLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.addView(searchWidget.getLayout(), params);

        //searchWidget.getSearchEditText().setOnFocusChangeListener(searchOnFocusListener);

        hasSearched = false;
        return rootView;
    }

    /**
     * Animates the search bar to the top of the screen.
     */
    private void moveSearchBar() {
        // Get the display dimensions and store in Point object
        Display d = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);

        int optionBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
        int[] position = new int[2]; // array to hold x y coordinates
        searchWidget.getLayout().getLocationOnScreen(position);

        searchWidget.getLayout().animate().x(position[0]).y(optionBarHeight).setDuration(500);
    }

    /**
     * Starts a QueryTask for a specified symbol.
     *
     * @param symbol the symbol of the company to retrieve data about
     */
    public void query(String symbol) {
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal2.add(Calendar.DAY_OF_YEAR, -1);

        if (symbol.equals("") || symbol == null) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noSymbolError), 3000).show();
            return;
        }
        // Format: Symbol & Last Trade & Change & Open & Volume & 52 Week Low & 52 Week High (15 min delay)
        final String stockURI = String.format(getString(R.string.stockURI).replaceAll(" ", "") + "s=%s&f=l1c1ovjkn",
                symbol);
        // Format: Symbol & Start Month & Start Day & Start Year & End Month & End Day & End Year
        // Months are from 0-11, 0 being January
        final String chartURI = String.format(getString(R.string.chartURI).replaceAll(" ", "") + "s=%s&a=%d&b=%s&c=%s&d=%d&e=%s&f=%s&n",
                symbol, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.YEAR));
        new QueryTask(StockFragment.this, symbol, stockURI, chartURI, new MainStockListener()).execute();
    }

    /**
     * This class is a concrete implementation of the StockListener interface. It allows for data communication
     * between the StockFragment and other classes that contain a StockListener.
     */
    private class MainStockListener implements StockListener {
        @Override
        public void onStockLoaded(List<StockData> stocksData) {
            if (!hasSearched) {
                moveSearchBar();
                hasSearched = true;
            }

            // if a stock view from a previous query exists, remove that view
            if (stockDataView != null) {
                layout.removeView(stockDataView.getScrollView());
            }

            stockDataView = createStockView(stocksData.get(0));
            RelativeLayout.LayoutParams params = createLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.layout.search_widget);
            layout.addView(stockDataView.getScrollView(), params);

            // bring search widget to front-most z index
            searchWidget.getLayout().bringToFront();

            // attempt to load the chart. If an error occurs while creating the chart, an exception will be thrown
            // and handled in a fail-safe way that does not force quit the application
            try {
                loadChart(stocksData);
            } catch (IOException e) {
                Log.e(TAG, getActivity().getString(R.string.chartError), e);
                Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.chartError), 3000);
                return;
            }
        }

        /**
         * Creates and sets up a stock view instance, given a stock.
         *
         * @param stock a StockData object containing details on a stock
         * @return a populated StockView instance
         */
        private StockView createStockView(StockData stock) {
            StockView stockDataView = new StockView(layout.getContext());
            stockDataView.getNameTextView().setText(stock.getName());
            stockDataView.getSymbolTextView().setText("(" + stock.getSymbol() + ")");
            stockDataView.getPriceTextView().setText(String.format("%.2f", stock.getPrice()));

            final StringBuilder builder = new StringBuilder(8);
            if (stock.getChange() < 0) {
                stockDataView.getChangeTextView().setTextColor(Color.RED);
            } else if (stock.getChange() > 0) {
                stockDataView.getChangeTextView().setTextColor(Color.GREEN);
                builder.append("+");
            } else {
                stockDataView.getChangeTextView().setTextColor(Color.BLACK);
            }
            builder.append(String.format("%.2f", stock.getChange()));
            stockDataView.getChangeTextView().setText(builder.toString());

            // Set up an animation for the stock view
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(500);
            stockDataView.getScrollView().setAnimation(fadeIn);

            return stockDataView;
        }

        /**
         * Loads a chart of historical stock data using Google Charts JavaScript API.
         * The HTML file data_chart.html must exist as the HTML file hosts the chart, which is
         * dynamically generated using Javascript and injected into the HTML file.
         *
         * @param stocksData a list of all the stocks retrieved from the search
         * @throws IOException
         */
        private void loadChart(List<StockData> stocksData) throws IOException {
            AssetManager assetManager = getActivity().getResources().getAssets();
            InputStream inputStream = assetManager.open(getActivity().getString(R.string.dataChartURI));
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            String content = new String(buffer, "UTF-8");
            inputStream.close();

            // dynamically insert values into HTML
            Object[] datePriceArray = new Object[stocksData.size() * 2];
            int stockIndex = stocksData.size() - 1;
            for (int i = 0; i < datePriceArray.length; i++) {
                if (i % 2 == 0) {
                    datePriceArray[i] = stocksData.get(stockIndex).getDate();
                } else if (i % 2 != 0) {
                    datePriceArray[i] = stocksData.get(stockIndex).getPrice();
                    stockIndex--;
                }
            }

            // after setting up on the backend, create and show the view with loaded data
            WebView webView = (WebView) getActivity().findViewById(R.id.chartWebView);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL(ASSET_PATH, String.format(content, datePriceArray), "text/html", "utf-8", null);
        }
    }


}
