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

        updateListView();

    }
}