package com.example.cafeteriaappmuc.BroadcastReceiver;


import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.cafeteriaappmuc.Activities.MainActivity;



public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private MainActivity mActivity;

    public SimWifiP2pBroadcastReceiver(MainActivity activity) {
        super();
        this.mActivity = activity;
    }

//called when either peers changed or group owner changed
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
         if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

             Toast.makeText(mActivity, "Peer list changed",
                    Toast.LENGTH_SHORT).show();
            mActivity.getNewBeacon();

            }

         //need this if phone connects to different beacon ie. goes to different cafeteria
         else if(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION.equals(action)) {

             SimWifiP2pInfo ginfo = (SimWifiP2pInfo) intent.getSerializableExtra(
                SimWifiP2pBroadcast.EXTRA_GROUP_INFO);
             ginfo.print();
            Toast.makeText(mActivity, "Group ownership changed",
                    Toast.LENGTH_SHORT).show();

            }
    }
}
