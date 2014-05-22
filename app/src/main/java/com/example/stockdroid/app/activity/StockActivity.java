package com.example.stockdroid.app.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.stockdroid.app.R;
import com.example.stockdroid.app.fragment.AboutFragment;
import com.example.stockdroid.app.fragment.AddStockFragment;
import com.example.stockdroid.app.fragment.PortfolioFragment;
import com.example.stockdroid.app.fragment.StockFragment;
import com.example.stockdroid.app.listener.PortfolioListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StockActivity extends Activity implements PortfolioListener {

    public static final String SHARED_PREFERENCES_NAME =
            "stock_activity_shared_preferences";
    private static final String MAIN_FRAGMENT_TAG = "MainFragment";

    private final ArrayList<String> portfolioStocks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new StockFragment(), MAIN_FRAGMENT_TAG)
                    .commit();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        if (map.size() > 0) {
            for (String symbol: map.keySet()) {
                portfolioStocks.add(symbol);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_portfolio:
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("stocks", portfolioStocks);
                PortfolioFragment fragment = new PortfolioFragment();
                fragment.setArguments(bundle);
                performFragmentTransaction(fragment);
                break;
            case R.id.action_about:
                performFragmentTransaction(new AboutFragment());
                break;
            case R.id.action_addStock:
                performFragmentTransaction(new AddStockFragment());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replaces the activity container with a new Fragment.
     *
     * @param fragment the fragment to replace the container
     */
    private void performFragmentTransaction(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public List<String> getPortfolioStocks() {
        return portfolioStocks;
    }

    @Override
    public void onStockAdded(String symbol) {
        getFragmentManager().popBackStack();
        if (!portfolioStocks.contains(symbol)) {
            portfolioStocks.add(symbol);
            Toast.makeText(StockActivity.this, getString(R.string.successfulAdd), 3000);
        }
        SharedPreferences sharedPreferences =
                getApplicationContext().getSharedPreferences(
                        StockActivity.SHARED_PREFERENCES_NAME,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        preferencesEditor.putString(symbol, symbol);
        preferencesEditor.apply();
    }

    @Override
    public void onStockSelected(String symbol) {
        getFragmentManager().popBackStack();
        ((StockFragment)getFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG)).query(symbol);
    }

}
