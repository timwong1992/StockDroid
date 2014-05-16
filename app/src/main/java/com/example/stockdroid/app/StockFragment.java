package com.example.stockdroid.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by tim on 5/13/14.
 */
public class StockFragment extends Fragment {

    private EditText searchEditText;

    public StockFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock, container, false);
        searchEditText = (EditText) rootView.findViewById(R.id.searchEditText);
        searchEditText.setHint(getString(R.string.defaultText));
        return rootView;
    }
}
