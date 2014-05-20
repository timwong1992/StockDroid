package com.example.stockdroid.app.task;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.listener.StockListener;
import com.example.stockdroid.app.stock.StockData;

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
import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * An AsyncTask that retrieves all the stock data from Yahoo Finance API.
 *
 * Created by tim on 5/16/14.
 */
public class QueryTask extends AsyncTask<String, Object, String> {

    enum TaskType {
        StockTask, ChartTask;
    }

    // Static constants
    private static final String TAG = "QueryTask.java";

    // Store a reference to the parent fragment that the task was run from
    private final Fragment parentFragment;

    // Store immutable copies of the URIs and symbol for quick reference
    private final String stockURI;
    private final String chartURI;
    private final String symbol;

    // Store a reference to a StockListener in order to pass data back to the fragment
    private final StockListener stockListener;

    // Data
    private List<StockData> stocksData;
    private String companyName;
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
        stocksData = new LinkedList<>();
        errorMsg = "";
        companyName = "";
    }

    @Override
    protected String doInBackground(String... params) {
        stocksData.addAll(getData(stockURI, TaskType.StockTask));
        stocksData.addAll(getData(chartURI, TaskType.ChartTask));
        return null;
    }

    /**
     * Retrieves data from a URI. This method is reusable with different URIs;
     * it can be used to retrieve fundamental stock data as well as chart data over multiple days.
     *
     * @param uri the URI of the data
     * @return data
     */
    private List<StockData> getData(final String uri, TaskType taskType) {
        final HttpClient client = new DefaultHttpClient();
        final HttpContext context = new BasicHttpContext();
        final HttpGet get = new HttpGet(uri);

        final List<StockData> data = new LinkedList<>();

        BufferedReader reader = null;

        try {
            final HttpResponse response = client.execute(get, context);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line;
            while ((line = reader.readLine()) != null) {
                final StockData stock = readLine(line, taskType);
                if (stock != null) {
                    data.add(stock);
                }
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
            return data;
        }
    }

    /**
     * Reads a line from getData().
     * If the line is part of a header tag and has a 404 Not Found error, throw a NotFoundException.
     *
     * @param line
     * @throws Resources.NotFoundException
     */
    private StockData readLine(final String line, TaskType taskType) throws Resources.NotFoundException, ParseException {
        if (line.contains(parentFragment.getActivity().getString(R.string.headerTag))
                && line.contains(parentFragment.getActivity().getString(R.string.error404))) {
            throw new Resources.NotFoundException();
        }
        if (line.contains("Date,Open,High")) {
            return null; // ignore CSV header
        }

        final String[] data = line.split(",");

        switch (taskType) {
            case StockTask:
                return new StockData(data[6], symbol, Calendar.getInstance(),
                        Double.parseDouble(data[0]), Double.parseDouble(data[2]), Double.parseDouble(data[1]),
                        Double.parseDouble(data[3]), Double.parseDouble(data[4]), Long.parseLong(data[5]));
            case ChartTask:
                return new StockData(companyName, symbol, data[0], Double.parseDouble(data[6]),
                    0, 0, 0, 0, Long.parseLong(data[5]));
            default:
                throw new Resources.NotFoundException();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (!errorMsg.equals("")) {
            Toast.makeText(parentFragment.getActivity().getApplicationContext(), errorMsg, 3000).show();
            return;
        }
        stockListener.onStockLoaded(stocksData);
    }

}
