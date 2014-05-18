package com.example.stockdroid.app;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by tim on 5/16/14.
 */
class QueryTask extends AsyncTask<String, Object, String> {
    private final Activity parentActivity;
    private final String uri;
    private String data;
    private static final String TAG = "QueryTask.java";

    public QueryTask(final Activity parentActivity, final String uri) {
        super();
        this.uri = uri;
        this.parentActivity = parentActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        data = getData();
        System.out.println(data);
        return data;
    }

    private String getData() {
        final StringBuilder builder = new StringBuilder(2048);
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
        } catch (UnknownHostException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(parentActivity.getApplicationContext(), parentActivity.getString(R.string.unableResolveHost), 3000).show();
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
     * Reads a line from getData(). If the line is part of the header tag and has a 404 Not Found error, throw a NotFoundException.
     *
     * @param builder
     * @param line
     * @throws Resources.NotFoundException
     */
    private void readLine(final StringBuilder builder, final String line) throws Resources.NotFoundException {
        if (line.contains(parentActivity.getString(R.string.headerTag)) && line.contains(parentActivity.getString(R.string.error404))) {
            throw new Resources.NotFoundException();
        }
        builder.append(line);
        builder.append("\n");
    }

    @Override
    protected void onPostExecute(String result) {
    }

}
