package com.example.cafeteriaappmuc.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.cafeteriaappmuc.Activities.MainActivity;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;

public class SimWifiP2pBroadcastReceiver extends BroadcastReceiver {

    private MainActivity mActivity;

    public SimWifiP2pBroadcastReceiver(MainActivity activity) {
        super();
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            //beacon in range of phone or beacon not in range of phone. List changed and should update server
            Toast.makeText(mActivity, "Peer list changed",
                    Toast.LENGTH_SHORT).show();

        }
    }
}
