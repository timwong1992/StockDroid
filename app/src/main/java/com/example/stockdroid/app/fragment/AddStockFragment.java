package com.example.stockdroid.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.listener.PortfolioListener;

/**
 * Created by tim on 5/21/14.
 */
public class AddStockFragment extends DialogFragment implements View.OnClickListener {

    private EditText addStockEditText;
    private Button addStockButton;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle argumentsBundle) {
        View rootView = inflater.inflate(R.layout.add_stock_dialog, container,
                false);

        addStockEditText = (EditText) rootView.findViewById(
                R.id.addStockEditText);

        addStockButton = (Button) rootView.findViewById(
                R.id.addStockButton);
        addStockButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        PortfolioListener listener = (PortfolioListener) getActivity();
        listener.onStockAdded(addStockEditText.getText().toString());
    }
}
