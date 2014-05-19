package com.example.stockdroid.app;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tim on 5/19/14.
 */
public class SearchWidget extends View {
    private final EditText searchEditText;
    private final Button searchButton;

    public SearchWidget(Context context) {
        super(context);
        searchEditText = (EditText) findViewById(R.id.searchEditText);
        searchButton = (Button) findViewById(R.id.searchButton);
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public Button getSearchButton() {
        return searchButton;
    }
}
