package com.example.coincollector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listView;

    public void downloadAndInsertCoin(final int year, final String rarity, final int quantity, final double value, String imageUrl, String dateAdded) {
        // Cette opération doit être effectuée dans un thread séparé pour ne pas bloquer l'UI Thread
        new Thread(() -> {
            try {
                // Utiliser Glide pour télécharger l'image et la convertir en Bitmap
                FutureTarget<Bitmap> target = Glide.with(MainActivity.this)
                        .asBitmap()
                        .load(imageUrl)
                        .submit();
                final Bitmap bitmap = target.get(); // Récupère le Bitmap téléchargé

                // Exécuter l'insertion dans la base de données dans le thread principal
                runOnUiThread(() -> {
                    db.insertCoin(year, rarity, quantity, value, bitmap,dateAdded);
                    updateListView(); // Mettre à jour la liste après insertion
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateListView() {
        List<Coin> coinList = db.getAllCoins();
        CoinAdapter adapter = new CoinAdapter(this, coinList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            updateListView(); // Rafraîchit la liste pour montrer les nouvelles pièces ajoutées
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnShowGraph = findViewById(R.id.showGraphButton);

        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.addCoinButton).setOnClickListener(view -> {
            Intent intent = new Intent(this, AddCoinActivity.class);
            startActivityForResult(new Intent(this, AddCoinActivity.class), 1);
        });

        db = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        //db.deleteAllCoins();
        String imageUrl = "https://img-3.journaldesfemmes.fr/a5LFTZ3qU2fUVOmwIVKDJawBJXA=/1500x/smart/83c0e4f55dd846dea2be0be27e715dcd/ccmcms-jdf/10662446.jpg";
        downloadAndInsertCoin(1234, "Rare", 312, 45, imageUrl,"2024-05-24");


        //if (db.getAllCoins().isEmpty()) {
        //    String imageUrl = "https://www.pieces-et-monnaies.com/cdn/shop/files/25ctlindauerdos.jpg?v=1691142845";
        //    downloadAndInsertCoin(1234, "Rare", 312, 45, imageUrl);
        //    String imageUrl2 = "https://www.pieces-et-monnaies.com/cdn/shop/files/guirauddos.png?v=1695805422";
        //    downloadAndInsertCoin(1574, "Commun", 56, 12, imageUrl2);
        //    String imageUrl3 = "https://www.pieces-et-monnaies.com/cdn/shop/files/cochetdos.png?v=1695807302";
        //    downloadAndInsertCoin(2001, "Rare", 71, 43, imageUrl3);
        //    String imageUrl4 = "https://www.pieces-et-monnaies.com/cdn/shop/files/guirauddos.png?v=1695805422";
        //    downloadAndInsertCoin(1092, "Peu rare", 132, 76, imageUrl4);
        //    String imageUrl5 = "https://www.pieces-et-monnaies.com/cdn/shop/files/cochetdos.png?v=1695807302";
        //    downloadAndInsertCoin(1234, "Commun", 1231, 4.5, imageUrl5);
        //    String imageUrl6 = "https://www.pieces-et-monnaies.com/cdn/shop/files/1_635a5b40-2e35-43cd-a79f-6adc03ca0357.png?v=1712050888";
        //    downloadAndInsertCoin(1783, "Très rare", 12, 300, imageUrl6);
        //}
        updateListView();

    }
}