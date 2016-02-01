package com.mohit.stockstracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohit on 21-04-2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context) {
        super(context, DATABASE, null, VERSION);
        // TODO Auto-generated constructor stub
    }

    public static final String DATABASE = "stocks";

    public static final int VERSION = 1;

    public static final String COMPANY_TABLE_NAME = "company";

    public static final String COMPANY_TICKER_COLUMN = "ticker";

    public static final String COMPANY_REMINDER_COLUMN = "reminder";

    public static final String COMPANY_ID = "_ID";



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + COMPANY_TABLE_NAME +
                " ( " + COMPANY_ID + " INTEGER PRIMARY KEY, "
                + COMPANY_REMINDER_COLUMN + " TEXT, "
                + COMPANY_TICKER_COLUMN + " TEXT)");
        ContentValues cv = new ContentValues();
        cv.put(DBOpenHelper.COMPANY_TICKER_COLUMN, "fb");
        db.insert(DBOpenHelper.COMPANY_TABLE_NAME, null, cv);

        ContentValues cv2 = new ContentValues();
        cv2.put(DBOpenHelper.COMPANY_TICKER_COLUMN, "goog");
        db.insert(DBOpenHelper.COMPANY_TABLE_NAME, null, cv2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stu
    }
}
