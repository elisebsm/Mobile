package com.example.cafeteriaappmuc.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.net.wifi.WifiManager;

import android.widget.Toast;

public class WifiReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int wifiStateExtra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);

        switch (wifiStateExtra) {
            case WifiManager.WIFI_STATE_ENABLED:

                System.out.println("Wifi is on");
                Toast.makeText(context, "Wifi on", Toast.LENGTH_SHORT).show();
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                System.out.println("wifi off");
                Toast.makeText(context, "Wifi off", Toast.LENGTH_SHORT).show();
                break;
        }
    }
};




/*
    private static class Task extends AsyncTask<String, Integer, String> {

        private static final String TAG = "MybroadcastReceiver";
        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }


*/




