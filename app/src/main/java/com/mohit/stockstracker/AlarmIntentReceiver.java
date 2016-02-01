package com.mohit.stockstracker;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mohit on 26-04-2015.
 */
public class AlarmIntentReceiver extends BroadcastReceiver implements StockFetcherAsyncTask.StockFetcherAsyncTaskInterface{

    Context context;
    ArrayList<Company> list;

    private String getURLString(Set<String> currentCompanies) {

        String s = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22";
        if (currentCompanies == null) {
            currentCompanies = new HashSet<>();
        }
        if(currentCompanies.size()== 0) {
            currentCompanies.add("goog");
            currentCompanies.add("fb");
        }
        if(currentCompanies.size()== 1) {
            if(currentCompanies.contains("goog")) {
                currentCompanies.add("fb");
            } else {
                currentCompanies.add("goog");
            }
        }

        boolean first = true;
        for (String ticker: currentCompanies) {
            if (first) {
                s = s + ticker;
                first = false;
                continue;
            }
            s = s + "%22%2C%22" + ticker;
        }
        return s + "%22)%0A%09%09&env=http%3A%2F%2Fdatatables.org%2Falltables.env&format=json";
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        DBOpenHelper mHelper = new DBOpenHelper(context);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        String[] columns = {
                DBOpenHelper.COMPANY_TICKER_COLUMN
        };

        Cursor c = db.query(DBOpenHelper.COMPANY_TABLE_NAME,
                columns, null, null, null, null, null);

        Set<String> currentCompanies = new HashSet<>();
        while (c.moveToNext()) {
            String t = c.getString(c.getColumnIndex(DBOpenHelper.COMPANY_TICKER_COLUMN));
            currentCompanies.add(t);
        }
        c.close();
        String urlString = getURLString(currentCompanies);
        StockFetcherAsyncTask task = new StockFetcherAsyncTask(context,true);
        task.delegate = this;
        task.execute(urlString);

    }

    @Override
    public void processResult(Company[] companies) {
        if (companies == null || companies.length == 0) {
            return;
        }

        list.clear();
        Collections.addAll(list, companies);
        DBOpenHelper mHelper = new DBOpenHelper(context);
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SharedPreferences s = context.getSharedPreferences("blah", Context.MODE_PRIVATE);
        Set<String> reminderTickers = s.getStringSet("reminder",null);
        for(Company str: list) {

            if(reminderTickers.contains(str.ticker)) {

                String[] columns = {DBOpenHelper.COMPANY_REMINDER_COLUMN};
                Cursor c = db.query(DBOpenHelper.COMPANY_TABLE_NAME,columns,new String("ticker" + " ='" + str +"'"),null,null,null,null);
                c.moveToFirst();
                String remPrice = c.getString(c.getColumnIndex(DBOpenHelper.COMPANY_REMINDER_COLUMN));
                if(Long.parseLong(str.price) > Long.parseLong(remPrice)) {

                    NotificationCompat.Builder b = new NotificationCompat.Builder(context);
                    b.setContentTitle(str.name + " Alert");
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(0, b.build());
                    break;
                }

            }

        }
    }
}
