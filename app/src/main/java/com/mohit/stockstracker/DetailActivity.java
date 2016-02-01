package com.mohit.stockstracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by Mohit on 25-04-2015.
 */
public class DetailActivity extends ActionBarActivity implements DetailHistoricalAsyncTask.DetailHistoricalAsyncTaskInterface,DetailRealTimeAsyncTask.DetailRealTimeAsyncTaskInterface{

    String mTicker;
    CompanyGraph[] arr;
    Button yearly;
    Button monthly;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity_layout);
        Intent i = getIntent();
        arr = new CompanyGraph[365];
        mTicker = i.getStringExtra("ticker");
        DetailHistoricalAsyncTask task = new DetailHistoricalAsyncTask(this);
        task.delegate = this;
        task.execute(getURLString());

        DetailRealTimeAsyncTask realTimeTask = new DetailRealTimeAsyncTask(this);
        realTimeTask.delegate = this;
        realTimeTask.execute(mTicker);

        yearly = (Button) findViewById(R.id.yearlyclick);
        monthly = (Button) findViewById(R.id.monthlyclick);


        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    monthly.setPressed(false);
//                    yearly.setPressed(true);
                    GraphView graph = (GraphView) findViewById(R.id.graph);

                    int a[] = new int[22];
                    ArrayList<DataPoint> BC = new ArrayList<DataPoint>();

                    for (int i = 0; i < 365; i++) {
                        DataPoint ab = new DataPoint(i, Double.parseDouble(arr[i].close));
                        BC.add(ab);
                    }

                    DataPoint[] b = BC.toArray(new DataPoint[BC.size()]);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(b);
                    graph.getViewport().setXAxisBoundsManual(true);

                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(360);
                    graph.getViewport().setMinY(0);
                    graph.getViewport().setMaxY(360);
                    graph.addSeries(series);

            }
        });

        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    yearly.setPressed(false);
//                    monthly.setPressed(true);
                    GraphView graph = (GraphView) findViewById(R.id.graph);

                    ArrayList<DataPoint> BC = new ArrayList<DataPoint>();

                    for (int i = 0; i < 30; i++) {
                        DataPoint ab = new DataPoint(i, Double.parseDouble(arr[i].close));
                        BC.add(ab);
                    }

                    DataPoint[] b = BC.toArray(new DataPoint[BC.size()]);

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(b);
                    graph.getViewport().setXAxisBoundsManual(true);

                    graph.getViewport().setMinX(0);
                    graph.getViewport().setMaxX(36);
                    graph.getViewport().setMinY(0);
                    graph.getViewport().setMaxY(36);
                    graph.addSeries(series);

            }
        });
    }

    private String getURLString() {
        AssetsDBOpenHelper openHelper = new AssetsDBOpenHelper(this);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = {
                DBContract.TICKER,
                DBContract.QUANDL
        };

        String selection = DBContract.TICKER + " ='" + mTicker.toUpperCase() + "'";

        Cursor c = db.query("nasdaq", columns, selection, null, null, null, null);
        c.moveToFirst();
        String quandl =
                c.getString(c.getColumnIndex(DBContract.QUANDL));
        String URLString = "http://www.quandl.com/api/v1/datasets/" + quandl + "?auth_token=1kRce7zh5GyKGWPX2EyT" ;
        return URLString;
    }

    @Override
    public void processResult(CompanyGraph[] company) {

    if(company==null)
    {
        return;
    }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        int a[]=new int [22];
        ArrayList<DataPoint> BC = new ArrayList<DataPoint>();

        for(int i=0; i < company.length;i++)
        {
            DataPoint ab=new DataPoint(i,Double.parseDouble(company[i].close));
            BC.add(ab);
        }

        DataPoint []b=BC.toArray(new DataPoint[BC.size()]);
        for(int i=0;i<company.length;i++)
        {
            arr[i]=company[i];
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(b);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(360);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(360);
        graph.addSeries(series);
    }

    @Override
    public void processResult(Company company) {
        TextView priceTextView = (TextView) findViewById(R.id.priceTextView);
        TextView percentChangeTextView = (TextView) findViewById(R.id.percentChangeTextView);
        TextView updateTimeTextView = (TextView) findViewById(R.id.updateTimeTextView);
        TextView open = (TextView) findViewById(R.id.openValue);
        TextView highValue = (TextView) findViewById(R.id.highValue);
        TextView lowValue = (TextView) findViewById(R.id.lowValue);
        TextView annualHighValue = (TextView) findViewById(R.id.annualHighValue);
        TextView annualLowValue = (TextView) findViewById(R.id.annualLowValue);
        TextView volumeValue = (TextView) findViewById(R.id.volumeValue);
        open.setText(company.open);
        priceTextView.setText("$" + company.price);
        percentChangeTextView.setText(company.percentageChange + " " + company.valueChange);
        if(company.percentageChange.charAt(0) == '-') {
            percentChangeTextView.setTextColor(Color.RED);
        } else {
            percentChangeTextView.setTextColor(Color.parseColor("#006400"));
        }
        highValue.setText(company.high);
        lowValue.setText(company.low);
        annualHighValue.setText(company.annualHigh);
        annualLowValue.setText(company.annualLow);
        volumeValue.setText(company.volume);
        String a = company.date.substring(0,10) + "  " + company.date.substring(11,16);
        updateTimeTextView.setText(a);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reminder:
                AlertDialog.Builder b = new AlertDialog.Builder(DetailActivity.this);
                b.setTitle("Set Reminder Price");

                LayoutInflater inflater = getLayoutInflater();
                final View output = inflater.inflate(R.layout.add_stock,null);
                b.setView(output);

                b.setPositiveButton("Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        EditText et = (EditText) output.findViewById(R.id.addNewEditText);
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        String reminder = et.getText().toString().toUpperCase();
                        DBOpenHelper mHelper = new DBOpenHelper(DetailActivity.this);
                        SQLiteDatabase db = mHelper.getReadableDatabase();
                        ContentValues cv = new ContentValues();

                        cv.put(DBOpenHelper.COMPANY_REMINDER_COLUMN ,  reminder);
                        db.update(DBOpenHelper.COMPANY_TABLE_NAME,cv,DBOpenHelper.COMPANY_TICKER_COLUMN + "='" + mTicker+"'",null);
                        Toast.makeText(DetailActivity.this,"Reminder Price Set !", Toast.LENGTH_SHORT).show();
                    }
                });
                b.create().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}