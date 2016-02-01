package com.mohit.stockstracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AssetsDBOpenHelper extends SQLiteOpenHelper {

	Context context;
	public AssetsDBOpenHelper(Context context) {
		super(context, "android_hackathon.db", null, 1);
		this.context = context;
		Utility.copyAssetDatabase(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
