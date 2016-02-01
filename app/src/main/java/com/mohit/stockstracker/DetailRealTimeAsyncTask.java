package com.mohit.stockstracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Mohit on 25-04-2015.
 */
public class DetailRealTimeAsyncTask extends AsyncTask<String, Integer, Company> {

    DetailRealTimeAsyncTaskInterface delegate;
    Context context;

    public DetailRealTimeAsyncTask (Context context) {
        this.context = context;
    }

    public interface DetailRealTimeAsyncTaskInterface {
        public void processResult(Company company);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

    }

    @Override
    protected Company doInBackground(String... arg0) {
        // TODO Auto-generated method stub
        String ticker = arg0[0];
        String urlString = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"
                + ticker + "%22)%0A%09%09&env=http%3A%2F%2Fdatatables.org%2Falltables.env&format=json";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Connection", "close");
            connection.connect();

            InputStream inStream = connection.getInputStream();

            if (inStream == null) {
                return null;
            }

            Scanner networkScanner = new Scanner(inStream);
            StringBuffer buffer = new StringBuffer();

            while (networkScanner.hasNext()) {
                String line = networkScanner.next();
                buffer.append(line);
            }

            networkScanner.close();
            return parseJSON(buffer.toString());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Company result) {
        // TODO Auto-generated method stub
        if(result == null) {
            return;
        }
        if (delegate != null)
            delegate.processResult(result);
    }

    public static Company parseJSON(String jsonString) throws JSONException {
        if(jsonString == null || jsonString.length() == 0) {
            return null;
        }
        JSONObject topLevel = new JSONObject(jsonString);
        JSONObject query = topLevel.getJSONObject("query");
        String date = query.getString("created");

        JSONObject results = query.getJSONObject("results");
        JSONObject quotes = results.getJSONObject("quote");

        String name = quotes.getString("Name");
        String ticker = quotes.getString("symbol");
        String price = quotes.getString("Ask");
        String percentageChange = quotes.getString("ChangeinPercent");
        String evaluation = quotes.getString("MarketCapitalization");
        String valueChange = quotes.getString("Change");
        String open = quotes.getString("Open");
        String high = quotes.getString("DaysHigh");
        String low = quotes.getString("DaysLow");
        String yearhigh = quotes.getString("YearHigh");
        String yearlow = quotes.getString("YearLow");
        String volume = quotes.getString("Volume");
        Company c = new Company(ticker, name, price, percentageChange, valueChange, evaluation,date);
        c.open = open;
        c.high = high;
        c.low = low;
        c.annualHigh = yearhigh;
        c.annualLow = yearlow;
        c.volume = volume;
        c.date=date;
        return c;
    }
}