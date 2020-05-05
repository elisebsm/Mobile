package com.example.cafeteriaappmuc.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;

import android.widget.Toast;

import com.example.cafeteriaappmuc.Activities.MainActivity;
import com.example.cafeteriaappmuc.NetworkStatus;

import java.util.concurrent.TimeUnit;

public class WifiReceiver extends BroadcastReceiver {

    //checks if device is connected to internet
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (NetworkStatus.checkNetworkConnection(context)) {
            if (NetworkStatus.checkWifiConnectivity(context)) {
                //Toast.makeText(context, "Wifi connected", Toast.LENGTH_SHORT).show();
                context.sendBroadcast(new Intent("WIFI_CONNECTED"));

            } else {
               // Toast.makeText(context, "Cellular network", Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }



}






