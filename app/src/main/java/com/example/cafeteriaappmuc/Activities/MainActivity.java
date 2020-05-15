package com.example.cafeteriaappmuc.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;


import android.os.AsyncTask;

import android.util.Log;

import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cafeteriaappmuc.Adapter.AdapterListViewMainFoodServices;

import com.example.cafeteriaappmuc.BroadcastReceiver.SimWifiP2pBroadcastReceiver;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.Objects.LineInfo;
import com.example.cafeteriaappmuc.MyDataListMain;
import com.example.cafeteriaappmuc.OpeningHours;
import com.example.cafeteriaappmuc.PermissionUtils;
import com.example.cafeteriaappmuc.QueueAlgorithm;
import com.example.cafeteriaappmuc.Objects.QueueInfo;
import com.example.cafeteriaappmuc.QueueTime;
import com.example.cafeteriaappmuc.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;

import android.os.IBinder;
import android.os.Messenger;


public class MainActivity extends AppCompatActivity implements Serializable, PeerListListener {

    private List<String> campusesAll = new ArrayList<>();

    //TODO: set current campus based on profile
    private String currentCampus = "";
    private static String hoursOpen;
    private Boolean isOpen;

    private ListView listViewFoodServices;
    private ArrayList<MyDataListMain> arrayList = new ArrayList<>();
    private AdapterListViewMainFoodServices adapterFoodServices;

    public int checkedDistanceToCampusCounter = 0;
    private int counterDisplayFoodServiceInList = 0;
    private int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private boolean locationEnabled = false;

    private List<String> services =new ArrayList<>();

    private SimWifiP2pManager mManager = null;
    private Channel mChannel = null;
    private boolean mBound = false;
    private SimWifiP2pBroadcastReceiver mReceiver;
    private String beaconInRange;
    private long timeArrivingQueueMillis;
    private long timeLeavingQueueMillis;
    private long timeInQueue;
    private int beaconDetectedCounter = 0;
    // creating a My HashTable Dictionary
    private Hashtable<String, Long> waitingTimeFoodservices = new Hashtable<String,Long>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getUserProfile() == null) {
            Toast.makeText(getApplicationContext(), "No user group selected. Please select user group under profile to display food services ", Toast.LENGTH_LONG).show();
        } else {
            //checking for internet connection
            if(checkNetworkConnection()) {
                checkDistanceToCampuses();
                //displayListForChoosingCampus();
            }
            else{
                displayListForChoosingCampus();
            }
        }
        enableMyLocation();

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
       // filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
       // filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        //will listen even though activity destroyd
        Intent intent = new Intent(this.getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

       // if (QueueTime.getWaitTime()==null){
         //   System.out.println("wait is not working");
       // }


    }

    private void setWaitingTimeFoodservices(String campus){
        if(services.size()!=0){
            //where services are the open cafeterias
            for (int i=0; i<services.size();i++){
                String foodserviceName = services.get(i);
                Double waitTime = QueueTime.getWaitTime(campus,foodserviceName);
                long roundedTime= Math.round(waitTime*10)/10;
                waitingTimeFoodservices.put(foodserviceName, roundedTime);
            }
        }


    }
    private Hashtable<String, Long> getWaitingTimeFoodservices(){
        return waitingTimeFoodservices;
    }




    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    public void getNewBeacon(){
        mManager.requestPeers(mChannel, MainActivity.this);
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        if(beaconDetectedCounter == 1){
            timeLeavingQueueMillis = System.currentTimeMillis();
            beaconDetectedCounter = 0;

            //time measured in miliseconds, find the time differene to get value of Y to add to list.
            //get the corresonding X value by getting the number of peers in queue in database
            Log.i("BEACONNAME  Arriving", String.valueOf(timeArrivingQueueMillis));
            Log.i("BEACONNAME  Leaving", String.valueOf(timeLeavingQueueMillis));


            timeInQueue = timeLeavingQueueMillis - timeArrivingQueueMillis;

            Log.i("BEACONNAME  InQueue", String.valueOf(timeInQueue));


        }
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            Log.i("BEACONNAME", device.deviceName);
            beaconInRange = device.deviceName;
            beaconDetectedCounter++;
            timeArrivingQueueMillis = System.currentTimeMillis();
        }



    }

/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

*/



    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onResume() {
        super.onResume();
        displayListForChoosingCampus();
        //displayDiningOptions(getUserProfile(), currentCampus);
        //displayMainFoodServicesList();
        if(getUserProfile()!= null){
            checkDistanceToCampuses();

            displayDiningOptions(getUserProfile(), currentCampus);
            displayMainFoodServicesList();
        }
    }


    private void checkDistanceToCampuses(){
        if(locationEnabled){
            LatLng latLngAlameda = new LatLng(38.736574, -9.139561);
            LatLng latLngTaguspark = new LatLng(38.737505, -9.302475);
            LatLng latLngCTN = new LatLng(	38.812522, -9.093773);
            getDistance(latLngAlameda);
            getDistance(latLngTaguspark);
            getDistance(latLngCTN);
        }
    }


    private void displayListForChoosingCampus(){
        Spinner spinnerListCampuses = findViewById(R.id.spinnerListOfCampus);
        if(!campusesAll.contains("Alameda")){
            campusesAll.add("Alameda");
        }
        if(!campusesAll.contains("Taguspark")){
            campusesAll.add("Taguspark");
        }
        if(!campusesAll.contains("CTN")){
            campusesAll.add("CTN");
        }
        List<String> campuses = removeCurrentCampusFromList(currentCampus);

        // Style and populate the spinner
        ArrayAdapter<String> campusAdapter;
        campusAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, campuses);

        // Dropdown layout style
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapterFoodServices to spinner
        spinnerListCampuses.setAdapter(campusAdapter);
        spinnerListCampuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapterView.getItemAtPosition(position).equals(getString(R.string.choose))) {
                    // do nothing
                } else {
                    counterDisplayFoodServiceInList = 0;

                    if (getUserProfile()==null){
                        Toast.makeText(getApplicationContext(), "No user group . Please select user group under profile to display food services ", Toast.LENGTH_LONG).show();

                    } else{
                        services.clear();
                        arrayList.clear();
                        Log.d("STATUS", getUserProfile());
                        displayCampusName(adapterView.getItemAtPosition(position).toString());
                        removeCurrentCampusFromList(currentCampus);
                        updateSpinner(adapterView.getItemAtPosition(position).toString());
                        displayDiningOptions(getUserProfile(), currentCampus);
                        displayMainFoodServicesList();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    //load menu options from profilemenu.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profilemenu, menu);
        return true;
    }

    //handle events on click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            showProfileSetup();
            return true;
        }
        return super.onContextItemSelected(item);
    }


    private void updateSpinner(String chosenCampus) {
        Spinner spinnerUpdatetList = findViewById(R.id.spinnerListOfCampus);
        List<String> campusesUpdated = removeCurrentCampusFromList(chosenCampus);

        // Style and populate the spinner
        ArrayAdapter campusAdapterUpdater;
        campusAdapterUpdater = new ArrayAdapter(this, android.R.layout.simple_spinner_item, campusesUpdated);

        // Dropdown layout style
        campusAdapterUpdater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapterFoodServices to spinner
        spinnerUpdatetList.setAdapter(campusAdapterUpdater);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayCampusName(String campusName) {
        currentCampus = campusName;
        TextView textViewMainCurrentCampusSet = findViewById(R.id.textViewMainCurrentCampus);
        textViewMainCurrentCampusSet.setText(campusName);

        displayDiningOptions(getUserProfile(), currentCampus);
//       TODO: show campus from what the user chose for the main campus
    }


    private List<String> removeCurrentCampusFromList(String currentCampus) {
        int numberOfCampuses = campusesAll.size();
        List<String> campuses = new ArrayList<>();
        campuses.add(0, getString(R.string.choose));
        for (int i = 0; i < numberOfCampuses; i++) {
            if (!campusesAll.get(i).equals(currentCampus)) {
                campuses.add(campusesAll.get(i));
            }
        }
        return campuses;
    }


    /**
     * Shows dining places based on status
     */
    // TODO: display dining options for taguspark as well
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayDiningOptions(String status, String campus) {
        //get openinghours list (only displays for alameda at the moment)
        if (campus.equals("Alameda")){
            OpeningHours openHours = new OpeningHours();
            List<String> foodServicesOpen ;
            foodServicesOpen= openHours.CafeteriasOpen(status,campus);
            List<String> foodServices= foodServicesOpen;
            services = foodServicesOpen;
            getDistanceValues(foodServices);
           // setWaitingTimeFoodservices(campus);
        }
        else if (campus.equals("Taguspark")){
            OpeningHours openHours = new OpeningHours();
            List<String> foodServicesOpen ;
            foodServicesOpen= openHours.CafeteriasOpen(status,campus);
            List<String> foodServices= foodServicesOpen;
            services = foodServicesOpen;
            getDistanceValues(foodServices);
            //setWaitingTimeFoodservices(campus);
        }
        else if (campus.equals("CTN")){
            OpeningHours openHours = new OpeningHours();
            List<String> foodServicesOpen ;
            foodServicesOpen= openHours.CafeteriasOpen(status,campus);
            List<String> foodServices= foodServicesOpen;
            services = foodServicesOpen;
            getDistanceValues(foodServices);
           // setWaitingTimeFoodservices(campus);
        }
    }


    // TODO: make the key work without hard coding it.
    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        return "https://maps.googleapis.com/maps/api/directions/json?" + str_org + "&" + str_dest + "&sensor=false&mode=walking&key=AIzaSyB72zLudOuMncMtCOCIpwgMVvTBLFAfPI8";
    }


    private void getDistance(LatLng latLngCampus){
        //TODO make actual currentlocation latLngCurrentLoc when delivering project
        /*LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();
        LatLng latLngCurrentLoc = new LatLng(userLat, userLong);*/

        //checking for internet connection
        if(checkNetworkConnection() && locationEnabled) {

            double userLatAl = 38.738300;
            double userLongAl = -9.139040;
            double userLatTa = 38.735580;
            double userLongTa = -9.299288;
            LatLng latLngCurrentLoc = new LatLng(userLatAl, userLongAl);

            String url = getRequestUrl(latLngCurrentLoc, latLngCampus);
            MainActivity.TaskRequestDistanceToCampuses taskRequestDistanceToCampuses = new MainActivity.TaskRequestDistanceToCampuses();
            taskRequestDistanceToCampuses.execute(url);
        }
        else{
            displayListForChoosingCampus();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationEnabled = true;

        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermissionMain(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }


    private void getDistanceValues(List<String> foodServices) {

        counterDisplayFoodServiceInList = 0;
        arrayList.clear();
        //TODO: change back to current after testing
        /* // Get current location
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();*/
        double userLat = 38.738300;
        double userLong = -9.139040;
        LatLng latLngCurrentLoc = new LatLng(userLat, userLong);

        List<LatLng> latLngs = new ArrayList<>();
        for (String foodService : foodServices) {
            if(locationEnabled && checkNetworkConnection()){
                if (foodService.equals("Central Bar")) {
                    double latitude = 	38.736606;
                    double longitude = -9.139532;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Civil Bar")) {
                    double latitude = 38.736988;
                    double longitude = -9.139955;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Civil Cafeteria")) {
                    double latitude = 38.737650;
                    double longitude = -9.140384;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Sena Pastry Shop")) {
                    double latitude = 38.737677;
                    double longitude = -9.138672;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Mechy Bar")) {
                    double latitude = 38.737247;
                    double longitude = -9.137434;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("AEIST Bar")) {
                    double latitude = 38.736542;
                    double longitude = -9.137226;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("AEIST Esplanade")) {
                    double latitude = 38.736318;
                    double longitude = -9.137820;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Chemy Bar")) {
                    double latitude = 38.736240;
                    double longitude = -9.138302;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("SAS Cafeteria")) {
                    double latitude = 38.736571;
                    double longitude = -9.137036;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Math Cafeteria")) {
                    double latitude = 38.735508;
                    double longitude = -9.139645;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Complex Bar")) {
                    double latitude = 38.736050;
                    double longitude = -9.140156;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Tagus Cafeteria")) {
                    double latitude = 38.737802;
                    double longitude = -9.303223;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Red Bar")) {
                    double latitude = 38.736546;
                    double longitude = -9.302207;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Green Bar")) {
                    double latitude = 38.738004;
                    double longitude = -9.303058;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("CTN Cafeteria")) {
                    double latitude = 38.812522;
                    double longitude = -9.093773;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("CTN Bar")) {
                    double latitude = 38.812522;
                    double longitude = -9.093773;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
            else{
                if (foodService.equals("Central Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Central Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Civil Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Civil Bar");
                    arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                    counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Civil Cafeteria")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Civil Cafeteria");
                    arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                    counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Sena Pastry Shop")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Sena Pastry Shop");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Mechy Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Mechy Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("AEIST Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("AEIST Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("AEIST Esplanade")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("AEIST Esplanade");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Chemy Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Chemy Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("SAS Cafeteria")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("SAS Cafeteria");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Math Cafeteria")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Math Cafeteria");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Complex Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Complex Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Tagus Cafeteria")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Tagus Cafeteria");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Red Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Red Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Green Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("Green Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("CTN Cafeteria")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("CTN Cafeteria");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("CTN Bar")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        long queue= getWaitingTimeFoodservices().get("CTN Bar");
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", queue));
                        counterDisplayFoodServiceInList++;
                    }
                }

            }

        }
    }


    private void displayMainFoodServicesList() {
        listViewFoodServices = findViewById(R.id.listViewFoodServices);
        adapterFoodServices = new AdapterListViewMainFoodServices(this, arrayList);
        listViewFoodServices.setAdapter(adapterFoodServices);
        adapterFoodServices.getItem(0);

        listViewFoodServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String foodService = arrayList.get(position).getFoodService();
                showFoodService(foodService);
            }
        });
    }

    private void showFoodService(String foodService) {
        Intent intentFoodService = new Intent(this, FoodServiceActivity.class);
        intentFoodService.putExtra("foodService", foodService);
        startActivity(intentFoodService);
    }
    //profile realted methods
    //starting profile setup activity
    private void showProfileSetup() {
        Intent intentProfileSetup = new Intent(this, ProfileSetupActivity.class);
        startActivity(intentProfileSetup);
    }


    //get user profile selected in profile
    private String getUserProfile() {

        final String key =getString(R.string.saved_profile_key);
        final String defValue = getString(R.string.saved_profile_default_key);
        SharedPreferences sharedPref = getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        String selectedUserProfile = sharedPref.getString(key, defValue);
        //check if profile is saved as something else than default
        if (selectedUserProfile.equals(getString(R.string.saved_profile_default_key))){
            return null;
        }
        else {
            //set global variable
            GlobalClass globalAssetsVariable = (GlobalClass) getApplicationContext();
            globalAssetsVariable.setProfile(selectedUserProfile);
            //  String val =globalAssetsVariable.getProfile();
            // Toast.makeText(getApplicationContext(), "Selected user"+val, Toast.LENGTH_LONG).show();
            return selectedUserProfile;
        }
    }


    // method to get direction using httpurlconnection
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response request
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return responseString;
    }

    //Beacon
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }





    /**
     * TaskRequestDirection and TarskParser are used for the AsyncTask to get time to walk
     * TODO: put them in their own class?? What if userdoes not allow user to use gps? fault handeling
     */
    // creates AsyncTask to call request Direction
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                if(checkNetworkConnection()){
                    responseString = requestDirection(strings[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        // parse json result
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse json result here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }



    public class TaskParser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject = null;
            String duration = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                duration = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").get("text").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return duration;
        }

        protected void onPostExecute(String duration) {
            Log.d("DURATION", duration);
            if(counterDisplayFoodServiceInList<=services.size()-1){
                String nameOfFoodservice = services.get(counterDisplayFoodServiceInList);
                //TODO get queuetime for service
                arrayList.add(new MyDataListMain(nameOfFoodservice, duration, 5));
                displayMainFoodServicesList();
                counterDisplayFoodServiceInList++;
            }
        }
    }


    /**
     * TaskRequestDistanceToCampuses and TarskParserDistance are used for the AsyncTask to
     * get distance to Taguspark and Alameda from current location
     * TODO: put them in their own class??
     */

    // creates AsyncTask to call request Direction
    public class TaskRequestDistanceToCampuses extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        // parse json result
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse json result here
            TaskParserDistance taskParserDistance = new TaskParserDistance();
            taskParserDistance.execute(s);
        }
    }


    public class TaskParserDistance extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            JSONObject jsonObject;
            String distance = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                distance = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").get("value").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return distance;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String distance) {
            Log.d("DISTANCE", distance);
            checkedDistanceToCampusCounter += 1;
            if (Integer.parseInt(distance) <= 1000 && checkedDistanceToCampusCounter==1){
                //displayChosenCampus("Alameda");
                currentCampus = "Alameda";
            } else if(Integer.parseInt(distance) <= 1000 && checkedDistanceToCampusCounter==2){
                //displayChosenCampus("Taguspark");
                currentCampus = "Taguspark";
            } else if(Integer.parseInt(distance) <= 1000 && checkedDistanceToCampusCounter==3){
                //displayChosenCampus("Taguspark");
                currentCampus = "CTN";
            }
            if (checkedDistanceToCampusCounter == 3){
                displayCampusName(currentCampus);
                displayListForChoosingCampus();
            }
        }
    }


    //checking if decvice is connected to internet
    private boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = null;

        capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if (capabilities == null)
            return false;
        else {
            int downloadBandwidth = capabilities.getLinkDownstreamBandwidthKbps();
            return downloadBandwidth >= 250;
        }
    }


}