package com.mohit.stockstracker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class StockFetcherAsyncTask extends AsyncTask<String, Integer, Company[]> {

    boolean listState;
	StockFetcherAsyncTaskInterface delegate;
	ProgressDialog dialog;
	Context context;

	public StockFetcherAsyncTask(Context context,boolean listState) {
        this.context = context;
        this.listState = listState;
	}
	
	public interface StockFetcherAsyncTaskInterface {
		public void processResult(Company[] companies);
	}
	
	@Override
	protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
        if (listState) {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading...Please wait");
            dialog.show();
         }
	}
	
	@Override
	protected Company[] doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String urlString = arg0[0];
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
	protected void onPostExecute(Company[] result) {
		// TODO Auto-generated method stub
        if(listState) {
            dialog.dismiss();
        }
		if (delegate != null)
			delegate.processResult(result);
	}

    public static Company[] parseJSON(String jsonString) throws JSONException {
        JSONObject topLevel = new JSONObject(jsonString);
        JSONObject query = topLevel.getJSONObject("query");
        String date = query.getString("created");
        JSONObject results = query.getJSONObject("results");
        JSONArray quotes = results.getJSONArray("quote");
        Company[] output = new Company[quotes.length()];
        for (int i = 0; i < quotes.length(); i++) {
            JSONObject company = quotes.getJSONObject(i);
            String name = company.getString("Name");
            String ticker = company.getString("symbol");
            String price = company.getString("Ask");
            String percentageChange = company.getString("ChangeinPercent");
            String valueChange = company.getString("Change");
            String evaluation = company.getString("MarketCapitalization");
            Company c = new Company(ticker, name, price, percentageChange, valueChange, evaluation,date);
            output[i] = c;
        }
        return output;
    }
}
