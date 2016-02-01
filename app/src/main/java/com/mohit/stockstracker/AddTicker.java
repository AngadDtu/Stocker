package com.mohit.stockstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

/**
 * Created by Mohit on 26-04-2015.
 */
public class AddTicker extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.addticker);
        Intent i = getIntent();
        Bundle b = i.getExtras();

         ArrayList<TickerStockExchange> list = b.getParcelableArrayList("listoftickers");
                        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.addNewEditText);
                textView.setThreshold(1);
                DialogArrayAdapter d = new DialogArrayAdapter(getApplicationContext(),
                        android.R.layout.simple_dropdown_item_1line,list);
                textView.setAdapter(d);
                d.notifyDataSetChanged();
    }
}
