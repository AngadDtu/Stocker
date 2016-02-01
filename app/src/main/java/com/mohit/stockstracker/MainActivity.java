package com.mohit.stockstracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends ActionBarActivity implements StockFetcherAsyncTask.StockFetcherAsyncTaskInterface {

    ArrayList<Company> list;

    ArrayList<String> mAllTickers;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerViewAdapter mAdapter;
    String mTicker;

    SwipeRefreshLayout mSwipeRefreshLayout;
    public static boolean listState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        mAllTickers = new ArrayList<>();
        updateData();

        AssetsDBOpenHelper openHelper = new AssetsDBOpenHelper(this);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] columns = {
                DBContract.TICKER
        };

//        String[] tableNames = {"bom","nse", "nasdaq"};
//        for(int i = 0 ; i < tableNames.length; i++) {
            Cursor c = db.query("nasdaq", columns, null, null, null, null, null);
            c.moveToFirst();
            while (c.moveToNext()) {
                String ticker =
                        c.getString(c.getColumnIndex(DBContract.TICKER));
                mAllTickers.add(ticker);
            }

        ButtonFloat buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Add New Ticker");

                LayoutInflater inflater = getLayoutInflater();
                final View output = inflater.inflate(R.layout.add_stock,null);
                b.setView(output);

                b.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        EditText et = (EditText) output.findViewById(R.id.addNewEditText);
                        String ticker = et.getText().toString().toUpperCase();
                        if(mAllTickers.contains(ticker)) {
                            saveDataToDB(ticker);
                            updateData();
                        } else {
                            Toast.makeText(getApplicationContext(),"Invalid Ticker !!" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

//                AutoCompleteTextView textView = (AutoCompleteTextView)output.findViewById(R.id.addNewEditText);
//                textView.setThreshold(1);
//                DialogArrayAdapter d = new DialogArrayAdapter(getApplicationContext(),
//                        android.R.layout.simple_dropdown_item_1line, mAllTickers);
//                textView.setAdapter(d);
//
//                mTicker = mAllTickers.get(textView.getListSelection()).ticker;
//                textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        saveDataToDB(mTicker);
//                        updateData();
//                    }
//                });
                b.create().show();

            }
        });


        AlarmManager alarmMgr =(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this,AlarmIntentReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30 * 1000, 3 * 60 * 1000, pendingAlarmIntent);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new RecyclerViewAdapter(list);
        mRecyclerView.setAdapter(mAdapter);

        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    DBOpenHelper mHelper = new DBOpenHelper(MainActivity.this);
                                    SQLiteDatabase db = mHelper.getWritableDatabase();
                                    db.delete(DBOpenHelper.COMPANY_TABLE_NAME,DBOpenHelper.COMPANY_TICKER_COLUMN + "='" + list.get(position).ticker+"'",null);
                                    list.remove(position);
                                }
                                // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                                mAdapter.notifyDataSetChanged();
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(getApplicationContext(),DetailActivity.class);
                        i.putExtra("ticker", list.get(position).ticker);
                        startActivity(i);
               //         Toast.makeText(MainActivity.this, "Clicked " + list.get(position).ticker, Toast.LENGTH_SHORT).show();
                    }
                }));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveDataToDB(String ticker) {
        DBOpenHelper mHelper = new DBOpenHelper(this);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBOpenHelper.COMPANY_TICKER_COLUMN, ticker);
        db.insert(DBOpenHelper.COMPANY_TABLE_NAME, null, cv);
        db.close();
    }

    private void updateData() {

        DBOpenHelper mHelper = new DBOpenHelper(this);
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
        StockFetcherAsyncTask task = new StockFetcherAsyncTask(this,listState);
        task.delegate = this;
        task.execute(urlString);

    }

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
    public void processResult(Company[] companies) {
        if (companies == null || companies.length == 0) {
            return;
        }
        if(listState) {
            listState = false;
        }
        list.clear();
        Collections.addAll(list, companies);
        mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }
    }
}