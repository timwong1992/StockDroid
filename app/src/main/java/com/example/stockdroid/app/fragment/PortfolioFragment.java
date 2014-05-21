package com.example.stockdroid.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.stock.StockData;

/**
 * Created by tim on 5/21/14.
 */
public class PortfolioFragment extends Fragment{

    private static final String TAG = "PortfolioFragment.java";
    private ListView listView;

    public PortfolioFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.portfolio_view, null);
        listView = (ListView) rootView.findViewById(R.id.portfolioListView);
        ArrayAdapter<StockData> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), 1);
        return null;
    }


}
