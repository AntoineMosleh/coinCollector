package com.example.coincollector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Coin {
    private int id;
    private int year;
    private String rarity;
    private int quantity;
    private double value;
    private byte[] image;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Bitmap getImageBitmap() {
        if (image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        return null;
    }
}