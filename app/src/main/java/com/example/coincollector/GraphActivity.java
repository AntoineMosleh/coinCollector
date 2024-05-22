package com.example.coincollector;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.components.XAxis;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        db = new DatabaseHelper(this);

        LineChart chart = findViewById(R.id.chart);
        List<Entry> entries = db.getChartData();

        LineDataSet dataSet = new LineDataSet(entries, "Valeur Totale des Pièces");
        dataSet.setColor(getResources().getColor(R.color.design_default_color_primary)); // Set the line color
        dataSet.setValueTextColor(getResources().getColor(R.color.design_default_color_on_primary)); // Set the color of value text
        dataSet.setValueTextSize(12f); // Set the size of value text

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false); // Disable the description on the chart
        chart.setTouchEnabled(true);
        chart.setPinchZoom(true); // Allow pinch to zoom

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Set X axis at the bottom
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DateAxisValueFormatter());

        chart.invalidate(); // Refresh the graph
    }

    /**
     * Custom formatter to convert float values into date strings
     */
    private class DateAxisValueFormatter extends ValueFormatter {
        private final SimpleDateFormat dateFormat;

        public DateAxisValueFormatter() {
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        }

        @Override
        public String getFormattedValue(float value) {
            long millis = (long) value * 24 * 60 * 60 * 1000;
            return dateFormat.format(new Date(millis + referenceDateMillis())); // Adjust reference date according to your date data
        }

        private long referenceDateMillis() {
            try {
                // Set a reference date based on your data
                Date referenceDate = dateFormat.parse("2000-01-01");
                return referenceDate.getTime();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error parsing date");
            }
        }
    }
}