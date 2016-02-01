package com.mohit.stockstracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;

public class Utility {
	final static int CHAR_BUF_SIZE = 1024;
	final static String DATABASE_NAME = "android_hackathon.db";
	
	public static Spanned getHTMLForFormula(final Context context, String text) {
		
		Html.ImageGetter getter = new Html.ImageGetter() {
			
			@Override
			public Drawable getDrawable(String source) {
				// TODO Auto-generated method stub
				if (source == null)
					return null;
				
				//source = source.replace(".svg", ".png");
				//source = source.replace("/src/main/assets/", "");
				Drawable d = null;
				try {
					AssetManager am = context.getAssets();
					InputStream is = am.open(source.replace("/src/main/assets/", ""));
					
				    //d = Drawable.createFromStream(is, null);
				    BitmapFactory.Options opts = new BitmapFactory.Options();
				    opts.inDensity = DisplayMetrics.DENSITY_HIGH;
					d = Drawable.createFromResourceStream(
							context.getResources(), null, is, source, opts);
					
				    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				    int i = 0;
				} catch(Exception e) {

				}
				return d;
			}
		};
		
		Spanned html = Html.fromHtml(text, getter, null);
		
		return html;
	}
	
	public static void copyAssetDatabase(Context context) {
		try {
			String db_dir = context.getApplicationInfo().dataDir + "/databases/";
			String db_file = db_dir + DATABASE_NAME;
		
			File deviceDb = new File(db_file);
			if (!deviceDb.exists()) {
				copyDataBase(context);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Copies the database file in asset folder to app folder
	 * 
	 * @throws java.io.IOException
	 */
	private static void copyDataBase(Context context) throws IOException {
		// open the local database
		InputStream copy = context.getAssets()
				.open(DATABASE_NAME);

		String db_dir = context.getApplicationInfo().dataDir + "/databases/";
		String db_file = db_dir + DATABASE_NAME;
		File dbPath = new File(db_dir);
		
		if (!dbPath.exists()) {
			dbPath.mkdir();
		}

		// Open the empty dbOut as the output stream
		OutputStream dbOut = new FileOutputStream(db_file);

		copyFile(copy, dbOut);
	}

	/**
	 * Copies given input stream to output stream
	 * 
	 * @param inputStream
	 * @param outputStream
	 * @throws java.io.IOException
	 */
	private static void copyFile(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		// copy database from the inputfile to the outputfile
		byte[] buffer = new byte[CHAR_BUF_SIZE];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		// Close the streams
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}

}
