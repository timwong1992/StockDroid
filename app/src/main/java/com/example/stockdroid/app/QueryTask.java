package com.example.stockdroid.app;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.StockListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * An AsyncTask that retrieves all the stock data from Yahoo Finance API.
 *
 * Created by tim on 5/16/14.
 */
public class QueryTask extends AsyncTask<String, Object, String> {
    // Static constants
    private static final String TAG = "QueryTask.java";
    private static final int BUFFER_SIZE = 512;

    // Store a reference to the parent fragment that the task was run from
    private final Fragment parentFragment;

    // Store immutable copies of the URIs and symbol for quick reference
    private final String stockURI;
    private final String chartURI;
    private final String symbol;

    // Store a reference to a StockListener in order to pass data back to the fragment
    private final StockListener stockListener;

    // Data
    private String stockData;
    private String chartData;
    private String errorMsg;

    /**
     * Constructor
     *
     * @param parentFragment
     * @param symbol
     * @param stockURI
     * @param chartURI
     * @param stockListener
     */
    public QueryTask(final Fragment parentFragment, final String symbol, final String stockURI,
                     final String chartURI, final StockListener stockListener) {
        super();
        this.stockURI = stockURI;
        this.chartURI = chartURI;
        this.symbol = symbol;
        this.parentFragment = parentFragment;
        this.stockListener = stockListener;
        stockData = null;
        chartData = null;
        errorMsg = "";
    }

    @Override
    protected String doInBackground(String... params) {
        stockData = getData(stockURI);
        chartData = getData(chartURI);
        return null;
    }

    /**
     * Retrieves data from a URI. This method is reusable with different URIs;
     * it can be used to retrieve fundamental stock data as well as chart data over multiple days.
     *
     * @param uri the URI of the data
     * @return data
     */
    private String getData(final String uri) {
        final StringBuilder builder = new StringBuilder(BUFFER_SIZE);
        final HttpClient client = new DefaultHttpClient();
        final HttpContext context = new BasicHttpContext();
        final HttpGet get = new HttpGet(uri);
        BufferedReader reader = null;

        try {
            final HttpResponse response = client.execute(get, context);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line;
            while ((line = reader.readLine()) != null) {
                readLine(builder, line);
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, e.toString());
            errorMsg = parentFragment.getActivity().getString(R.string.symbolNotFoundError);
        } catch (UnknownHostException e) {
            Log.e(TAG, e.toString());
            errorMsg = parentFragment.getActivity().getString(R.string.unableResolveHost);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
            return builder.toString();
        }
    }

    /**
     * Reads a line from getData().
     * If the line is part of a header tag and has a 404 Not Found error, throw a NotFoundException.
     *
     * @param builder
     * @param line
     * @throws Resources.NotFoundException
     */
    private void readLine(final StringBuilder builder, final String line) throws Resources.NotFoundException {
        if (line.contains(parentFragment.getActivity().getString(R.string.headerTag))
                && line.contains(parentFragment.getActivity().getString(R.string.error404))) {
            throw new Resources.NotFoundException();
        }
        builder.append(line);
        builder.append("\n");
    }

    @Override
    protected void onPostExecute(String result) {
        if (!errorMsg.equals("")) {
            Toast.makeText(parentFragment.getActivity().getApplicationContext(), errorMsg, 3000).show();
            return;
        }
        stockListener.onStockLoaded(symbol, stockData, chartData);
    }

}
