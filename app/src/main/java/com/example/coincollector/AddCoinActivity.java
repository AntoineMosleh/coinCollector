package com.example.coincollector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AddCoinActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageViewCoin;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coin);

        imageViewCoin = findViewById(R.id.imageViewCoin);
        Button buttonCapture = findViewById(R.id.buttonCapture);
        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        Button btnSave = findViewById(R.id.btnSave); // Assurez-vous d'ajouter ce bouton dans votre XML
        btnSave.setOnClickListener(v -> saveCoin());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageViewCoin.setImageBitmap(imageBitmap);
        }
    }

    private void saveCoin() {
        EditText editTextYear = findViewById(R.id.editTextYear);
        EditText editTextRarity = findViewById(R.id.editTextRarity);
        EditText editTextQuantity = findViewById(R.id.editTextQuantity);
        EditText editTextValue = findViewById(R.id.editTextValue);

        int year = Integer.parseInt(editTextYear.getText().toString());
        String rarity = editTextRarity.getText().toString();
        int quantity = Integer.parseInt(editTextQuantity.getText().toString());
        double value = Double.parseDouble(editTextValue.getText().toString());

        DatabaseHelper db = new DatabaseHelper(this);
        db.insertCoin(year, rarity, quantity, value, imageBitmap);

        // Définir le résultat OK pour notifier MainActivity que tout s'est bien passé
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);

        finish();
    }
}