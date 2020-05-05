package com.example.cafeteriaappmuc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkStatus {

    //checking if decvice is connected to internet
    static public boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = null;

        capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (capabilities == null)

            return false;
        else {

            int downloadBandwidth = capabilities.getLinkDownstreamBandwidthKbps();
            return downloadBandwidth >= 250;
        }
    }
    //checks wich type of internetConnection wifi/ or cellular
    public static Boolean checkWifiConnectivity(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkCapabilities capabilities = null;
        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

    }

}
