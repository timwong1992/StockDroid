package com.example.stockdroid.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.stockdroid.app.R;

/**
 * Created by tim on 5/19/14.
 */
public class SearchWidget extends View {
    private final RelativeLayout layout;
    private final EditText searchEditText;
    private final Button searchButton;

    public SearchWidget(Context context, AttributeSet attributeSet, RelativeLayout parentLayout) {
        super(context, attributeSet);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = (RelativeLayout) inflater.inflate(R.layout.search_widget, null);

        searchEditText = (EditText) layout.findViewById(R.id.searchEditText);
        searchButton = (Button) layout.findViewById(R.id.searchButton);
    }

    public RelativeLayout getLayout() {
        return layout;
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public Button getSearchButton() {
        return searchButton;
    }


}
