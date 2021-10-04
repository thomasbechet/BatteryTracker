package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BatteryService extends Service {

    // Broadcast signals
    final static String SERVICE_REQUEST = "SERVICE_REQUEST"; // Ask to send a callback signal with refreshed data
    final static String SERVICE_CALLBACK = "SERVICE_CALLBACK"; // Callback intent with the asked data
    final static String SERVICE_DATA_UPDATED = "SERVICE_DATA_UPDATED"; // Intent send when data is updated
    final static String SERVICE_RESET = "SERVICE_RESET"; // Intent send by the activity to reset saved data

    // Data identifier in the Intent
    final static String EXTRA_PERCENTAGES = "EXTRA_PERCENTAGES";

    // Save file containing lines to display
    final static String SAVE_FILE = "percentages.txt";

    // List of lines (not persistent)
    private ArrayList<String> lines;

    public BatteryService() {
        this.lines = new ArrayList<>();
    }

    // Save (append) a given line
    private void save(String line) {
        try {
            FileOutputStream out = openFileOutput(SAVE_FILE, Context.MODE_APPEND);
            out.write((line + "\n").getBytes());
            out.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // Read lines saved in the file
    private void read() {
        try {
            FileInputStream in = openFileInput(SAVE_FILE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            this.lines.clear();
            String s;
            while ((s = reader.readLine()) != null) {
                this.lines.add(s);
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Read lines from the file
        read();

        // Create the service broadcast with required signals
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(BatteryService.SERVICE_REQUEST);
        intentFilter.addAction(BatteryService.SERVICE_RESET);
        registerReceiver(new BatteryServiceReceiver(this), intentFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // New data coming from the broadcast
    public void registerPercentage(float percentage, Date time) {
        String line = Float.toString(Math.round(percentage)) + " % - " + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(time);
        this.lines.add(line);
        save(line); // Append the new line to the file
    }

    public void registerPowerStatus(boolean connected, Date time) {
        String status = connected ? "connected" : "disconnected";
        String line = "Power " + status + " - " + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(time);
        this.lines.add(line);
        save(line); // Append the new line to the file
    }

    // Recover current lines in memory
    public List<String> getLines() {
        return this.lines;
    }

    // Remove saved data from memory + persistent memory
    public void reset() {
        this.lines.clear(); // Memory

        // Clear the file opening the file (not append mode)
        try {
            FileOutputStream out = openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
            out.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
