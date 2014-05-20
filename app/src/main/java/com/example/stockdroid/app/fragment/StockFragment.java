package com.example.stockdroid.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.stockdroid.app.stock.StockData;
import com.example.stockdroid.app.task.QueryTask;
import com.example.stockdroid.app.R;
import com.example.stockdroid.app.view.SearchWidget;
import com.example.stockdroid.app.listener.StockListener;

import java.util.Calendar;
import java.util.List;

/**
 * THe main fragment to view stock information.
 *
 * Created by tim on 5/13/14.
 */
public class StockFragment extends Fragment {

    // GUI elements
    private RelativeLayout layout;
//    private EditText searchEditText;
    private SearchWidget searchWidget;
//    private Button searchButton;
    private boolean hasSearched;

    // OnClickListener to detect when query is launched. On default, gathers one week's worth of data.
    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            cal.add(Calendar.DAY_OF_YEAR, -1);
            cal2.add(Calendar.DAY_OF_YEAR, -1);

            final String symbol = searchWidget.getSearchEditText().getText().toString();
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

        searchWidget = new SearchWidget(rootView.getContext(), null, layout);
        searchWidget.getSearchButton().setOnClickListener(searchOnClickListener);

        RelativeLayout.LayoutParams params = createLayoutParams();
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        layout.addView(searchWidget.getLayout(), params);

        //searchWidget.getSearchEditText().setOnFocusChangeListener(searchOnFocusListener);

        hasSearched = false;
        return rootView;
    }

    /**
     * Animates the search bar to the top upon successfully loading stock data.
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

    private class MainStockListener implements StockListener {
        @Override
        public void onStockLoaded(List<StockData> stocksData) {
            if (!hasSearched) {
                moveSearchBar();
                hasSearched = true;
            }
            for (StockData stock: stocksData) {
                System.out.println(stock.getName() + "," + stock.getSymbol() + "," + stock.getDate() + "," + stock.getPrice() + "," + stock.getVolume());
            }
        }
    }

}
