package com.example.coincollector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coins.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "coins";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_IMAGE = "image";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_RARITY + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_VALUE + " REAL, " +
                COLUMN_IMAGE + " BLOB, " +
                "date_added TEXT DEFAULT (date('now'))" +
                ")";
        db.execSQL(createTable);
    }

    public void deleteCoin(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }

    public void updateCoin(int id, int year, String rarity, int quantity, double value, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_RARITY, rarity);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_IMAGE, image);

        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[] {String.valueOf(id)});
        db.close();
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN date_added TEXT");
            db.execSQL("UPDATE " + TABLE_NAME + " SET date_added = date('now')");
        }
    }

    public Bitmap getImageFromCursor(Cursor cursor) {
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    public void insertCoin(int year, String rarity, int quantity, double value, Bitmap image, String dateAdded) {
        byte[] imageData = getBytesFromBitmap(image);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_RARITY, rarity);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_IMAGE, imageData);
        values.put("date_added", dateAdded);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteAllCoins() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    public List<Coin> getAllCoins() {
        List<Coin> coinList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Coin coin = new Coin();
                coin.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                coin.setYear(cursor.getInt(cursor.getColumnIndex(COLUMN_YEAR)));
                coin.setRarity(cursor.getString(cursor.getColumnIndex(COLUMN_RARITY)));
                coin.setQuantity(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
                coin.setValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_VALUE)));
                byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
                if (imageBytes != null) {
                    coin.setImage(imageBytes);
                }

                coinList.add(coin);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return coinList;
    }

    public List<Entry> getChartData() {
        List<Entry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT date_added, SUM(value) AS total_value FROM " + TABLE_NAME + " GROUP BY date_added ORDER BY date_added";
        Cursor cursor = db.rawQuery(query, null);

        float cumulativeTotal = 0;
        if (cursor.moveToFirst()) {
            do {
                String dateStr = cursor.getString(cursor.getColumnIndex("date_added"));
                float dailyTotal = cursor.getFloat(cursor.getColumnIndex("total_value"));
                cumulativeTotal += dailyTotal; // Procède au cumul
                long dateMillis = convertDateToMillis(dateStr);
                entries.add(new Entry(dateMillis, cumulativeTotal));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }

    private long convertDateToMillis(String dateStr) {
        if (dateStr == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }



}