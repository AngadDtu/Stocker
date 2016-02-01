package com.mohit.stockstracker;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohit on 21-04-2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder> {

    ArrayList<Company> companies;

    public RecyclerViewAdapter(ArrayList<Company> companies) {
        this.companies = companies;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item
                , parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Company c = companies.get(position);
        holder.tickerTextView.setText(c.ticker.toUpperCase());
        holder.companyNameTextView.setText(c.name);
        holder.priceTextView.setText("$" + c.price);
        holder.percentageChangeTextView.setText(c.percentageChange);
        if(c.percentageChange.charAt(0) == '-') {
            holder.percentageChangeTextView.setBackgroundColor(Color.RED);
        } else {
            holder.percentageChangeTextView.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView tickerTextView;
        private TextView companyNameTextView;
        private TextView priceTextView;
        private TextView percentageChangeTextView;

        public CustomViewHolder(View itemView) {
            super(itemView);

            tickerTextView = (TextView) itemView.findViewById(R.id.tickerTextView);
            companyNameTextView = (TextView) itemView.findViewById(R.id.companyNameTextView);
            priceTextView = (TextView) itemView.findViewById(R.id.priceTextView);
            percentageChangeTextView = (TextView) itemView.findViewById(R.id.percentChangeTextView);
        }
    }
}
