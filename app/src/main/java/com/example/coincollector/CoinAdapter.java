package com.example.coincollector;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CoinAdapter extends ArrayAdapter<Coin> {
    public CoinAdapter(Context context, List<Coin> coins) {
        super(context, 0, coins);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_coin, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView textYearRarity = convertView.findViewById(R.id.text_year_rarity);
        TextView textQuantityValue = convertView.findViewById(R.id.text_quantity_value);

        Coin coin = getItem(position);


        textYearRarity.setText("Year: " + coin.getYear() + ", Rarity: " + coin.getRarity());
        textQuantityValue.setText("Quantity: " + coin.getQuantity() + ", Value: $" + coin.getValue());

        Bitmap bitmap = coin.getImageBitmap();
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.deuxeurocoin);
        }

        return convertView;
    }
}