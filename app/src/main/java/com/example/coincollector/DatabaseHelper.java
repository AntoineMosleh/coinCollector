package com.example.coincollector;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "coins.db";
    private static final int DATABASE_VERSION = 2;

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
                "UNIQUE (" + COLUMN_ID + "))";
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
        // Mettez à jour votre schéma de base de données ici pour ajouter la colonne image
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_IMAGE + " BLOB DEFAULT ''");
        }
    }

    public Bitmap getImageFromCursor(Cursor cursor) {
        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    public void insertCoin(int year, String rarity, int quantity, double value, Bitmap image) {
        byte[] imageData = getBytesFromBitmap(image);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_RARITY, rarity);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_VALUE, value);
        values.put(COLUMN_IMAGE, imageData);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteAllCoins() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME); // Supprime toutes les données de la table
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
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    coin.setImage(imageBytes);
                }

                coinList.add(coin);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return coinList;
    }
}