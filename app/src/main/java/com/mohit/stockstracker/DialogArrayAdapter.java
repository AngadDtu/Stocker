package com.mohit.stockstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohit on 25-04-2015.
 */
public class DialogArrayAdapter extends ArrayAdapter<TickerStockExchange> {

    Context mContext;
    ArrayList<TickerStockExchange> mList;
    public DialogArrayAdapter(Context context, int resource, ArrayList<TickerStockExchange> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TickerStockExchange item = getItem(position);

        if(convertView==null)
        {
        LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.dialog_list_item, parent, false);
        }

        TextView ticker = (TextView) convertView.findViewById(R.id.tickerTextView);
        ticker.setText(item.ticker);
        return convertView;
    }
}
