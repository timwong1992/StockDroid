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
 * Created by tim on 5/13/14.
 */
public class StockFragment extends Fragment {

    private EditText searchEditText;
    private Button searchButton;

    private View.OnClickListener searchOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_YEAR, -1);

            final String symbol = searchEditText.getText().toString();
            if (symbol.equals("") || symbol == null) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), getString(R.string.noSymbolError), 3000);
                toast.show();
                return;
            }

            final String uri = String.format(getString(R.string.dataURI) + "s=%s&a=%d&b=%s&c=%s&d=%d&e=%s&f=%s",
                    symbol, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.YEAR));
            System.out.println(uri);
            new QueryTask(getActivity(), uri).execute();
        }
    };

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




}
