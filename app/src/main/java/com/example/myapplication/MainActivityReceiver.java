package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainActivityReceiver extends BroadcastReceiver {

    private MainActivity activity;

    public MainActivityReceiver(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Callback and data updated is the same but it might be optimal to
        // send only updated data and change only related string views
        if (intent.getAction().equals(BatteryService.SERVICE_CALLBACK)) {
            String data = intent.getStringExtra(BatteryService.EXTRA_PERCENTAGES);
            String[] lines = data.split("¤"); // Uncompress lines
            this.activity.updateLines(lines);
        } else if (intent.getAction().equals(BatteryService.SERVICE_DATA_UPDATED)) {
            String data = intent.getStringExtra(BatteryService.EXTRA_PERCENTAGES);
            String[] lines = data.split("¤"); // Uncompress lines
            this.activity.updateLines(lines);
        }
    }
}
