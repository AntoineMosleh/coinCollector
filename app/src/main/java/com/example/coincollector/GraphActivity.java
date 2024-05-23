package com.example.coincollector;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.List;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        LineChart chart = findViewById(R.id.chart);
        DatabaseHelper db = new DatabaseHelper(this);
        List<Entry> entries = db.getChartData();  // Assurez-vous que cette méthode renvoie les données appropriées

        LineDataSet dataSet = new LineDataSet(entries, "Valeur Totale des Pièces");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();  // Rafraîchit le graphique pour montrer les données
    }
}
