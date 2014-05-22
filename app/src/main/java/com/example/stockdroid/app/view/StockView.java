package com.example.stockdroid.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.stockdroid.app.R;

/**
 * Created by tim on 5/20/14.
 */
public class StockView extends ScrollView {

    private final ScrollView scrollView;
    private final LinearLayout layout;
    private final TextView nameTextView;
    private final TextView symbolTextView;
    private final TextView priceTextView;
    private final TextView changeTextView;
    private final Button addStockButton;


    public StockView(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        scrollView = (ScrollView) inflater.inflate(R.layout.data_view, null);
        layout = (LinearLayout) scrollView.findViewById(R.id.stockViewLayout);
        nameTextView = (TextView) layout.findViewById(R.id.nameTextView);
        symbolTextView = (TextView) layout.findViewById(R.id.symbolTextView);
        priceTextView = (TextView) layout.findViewById(R.id.priceTextView);
        changeTextView = (TextView) layout.findViewById(R.id.changeTextView);
        addStockButton = (Button) layout.findViewById(R.id.addStockButton);
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public TextView getPriceTextView() {
        return priceTextView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public ScrollView getScrollView() {
        return scrollView;
    }

    public TextView getSymbolTextView() {
        return symbolTextView;
    }

    public TextView getChangeTextView() {
        return changeTextView;
    }
}
