package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> percentages;
    private ListAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Allocate display list
        this.percentages = new ArrayList<>();

        // Start the service
        Intent myIntent = new Intent(MainActivity.this, BatteryService.class);
        this.startService(myIntent);

        // Setup list view adapter
        this.listViewAdapter = new ArrayAdapter<>(this, R.layout.row_layout, R.id.textView, this.percentages);
        ListView listView = (ListView) findViewById(R.id.SUPER_LIST_VIEW);
        listView.setAdapter(this.listViewAdapter);

        // Setup receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BatteryService.SERVICE_CALLBACK);
        intentFilter.addAction(BatteryService.SERVICE_DATA_UPDATED);
        registerReceiver(new MainActivityReceiver(this), intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent requestIntent = new Intent();
        requestIntent.setAction(BatteryService.SERVICE_REQUEST);
        sendBroadcast(requestIntent);
    }

    public void updateLines(String[] lines) {
        // Simply refresh everything
        this.percentages.clear();
        this.percentages.addAll(Arrays.asList(lines));
        ((ArrayAdapter)this.listViewAdapter).notifyDataSetChanged();
    }

    public void resetView(View view) {
        this.percentages.clear();
        ((ArrayAdapter)this.listViewAdapter).notifyDataSetChanged();
        Intent requestIntent = new Intent();
        requestIntent.setAction(BatteryService.SERVICE_RESET);
        sendBroadcast(requestIntent);
    }
}