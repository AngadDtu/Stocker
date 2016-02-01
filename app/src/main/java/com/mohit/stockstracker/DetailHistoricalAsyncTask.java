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
public class DetailHistoricalAsyncTask extends AsyncTask<String, Integer, CompanyGraph[]> {

    DetailHistoricalAsyncTaskInterface delegate;
    ProgressDialog dialog;
    Context context;

    public DetailHistoricalAsyncTask(Context context) {
        this.context = context;
    }

    public interface DetailHistoricalAsyncTaskInterface {
        public void processResult(CompanyGraph[] company);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...Please wait");
        dialog.show();

    }

    @Override
    protected CompanyGraph[] doInBackground(String... arg0) {
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
    protected void onPostExecute(CompanyGraph[] result) {
        // TODO Auto-generated method stub
        dialog.dismiss();

        if (delegate != null)
            delegate.processResult(result);
    }

    public static CompanyGraph[] parseJSON(String jsonString) throws JSONException {
        JSONObject topLevel = new JSONObject(jsonString);
        JSONArray data = topLevel.getJSONArray("data");
        CompanyGraph[] output = new CompanyGraph[365];
        for (int i = 0; i < 365; i++) {
            JSONArray timedata = data.getJSONArray(i);
            String date="",open="",high="",low="",close="",volume="";
            for(int j=0;j<timedata.length();j++)
            {
                if(j==0)
                {
                    date=timedata.getString(j);
                }
                else if(j==1)
                {
                    double d1 = timedata.getDouble(j);
                    open=d1+"";

                }else if(j==2)
                {
                    double d2 = timedata.getDouble(j);
                    high=d2+"";
                }
                else if(j==3)
                {
                    double d3 = timedata.getDouble(j);
                    low=d3+"";
                }
                else if(j==4)
                {
                    double d4 = timedata.getDouble(j);
                    close=d4+"";
                }

                else if(j==timedata.length()-1)
                {
                    long v = timedata.getLong(j);
                    volume=v+"";
                }
            }
            CompanyGraph c = new CompanyGraph(date, open, high, low, close, volume);
            output[i] = c;
        }
        int a = 0;
        int b = 9;
        a=b;
        b=a;


        return output;
    }
}
