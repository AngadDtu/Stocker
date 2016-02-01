package com.mohit.stockstracker;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mohit on 25-04-2015.
 */
public class TickerStockExchange implements Parcelable {
    String ticker;
    String stockExchange;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { this.ticker, this.stockExchange});
    }

}
