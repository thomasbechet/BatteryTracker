package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

public class BatteryServiceReceiver extends BroadcastReceiver {

    private BatteryService batteryService;

    public BatteryServiceReceiver(BatteryService service) {
        this.batteryService = service;
    }

    // Send a intent with update data (for simplicity, we simply send everything)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendDataUpdated(Context context) {
        String data = this.batteryService.getLines().stream().collect(Collectors.joining("¤"));
        Intent dataUpdatedIntent = new Intent();
        dataUpdatedIntent.putExtra(BatteryService.EXTRA_PERCENTAGES, data);
        dataUpdatedIntent.setAction(BatteryService.SERVICE_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
    }

    // Send all data in memory (called when the activity is resumed or created)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendDataRefresh(Context context) {
        String data = this.batteryService.getLines().stream().collect(Collectors.joining("¤"));
        Intent dataUpdatedIntent = new Intent();
        dataUpdatedIntent.putExtra(BatteryService.EXTRA_PERCENTAGES, data);
        dataUpdatedIntent.setAction(BatteryService.SERVICE_CALLBACK);
        context.sendBroadcast(dataUpdatedIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale * 100.0f;
            this.batteryService.registerPercentage(batteryPct, Calendar.getInstance().getTime());
            sendDataUpdated(context);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
            this.batteryService.registerPowerStatus(true, Calendar.getInstance().getTime());
            sendDataUpdated(context);
        } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            this.batteryService.registerPowerStatus(false, Calendar.getInstance().getTime());
            sendDataUpdated(context);
        } else if (intent.getAction().equals(BatteryService.SERVICE_REQUEST)) {
            sendDataRefresh(context);
        } else if (intent.getAction().equals(BatteryService.SERVICE_RESET)) {
            this.batteryService.reset();
        }
    }
}
