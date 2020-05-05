package com.example.cafeteriaappmuc.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;


import android.os.AsyncTask;

import android.os.Messenger;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.example.cafeteriaappmuc.Adapter.AdapterListViewMainFoodServices;
import com.example.cafeteriaappmuc.Adapter.RecyclerViewAdapter;
import com.example.cafeteriaappmuc.BroadcastReceiver.WifiReceiver;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.ImageUploadInfo;
import com.example.cafeteriaappmuc.MyDataListMain;
import com.example.cafeteriaappmuc.OpeningHours;
import com.example.cafeteriaappmuc.PermissionUtils;
import com.example.cafeteriaappmuc.R;

import com.example.cafeteriaappmuc.SimWifiP2pBroadcastReceiver;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

public class MainActivity extends AppCompatActivity implements Serializable, SimWifiP2pManager.PeerListListener {

    private List<String> campusesAll = new ArrayList<>();

    //TODO: set current campus based on profile
    private String currentCampus = "";
    //private String status;

    /**
     * copied from lab 4
     */
    private SimWifiP2pManager manager = null;
    private SimWifiP2pManager.Channel channel = null;
    private Messenger service = null;
    private boolean bound = false;
    private SimWifiP2pSocketServer socketServer = null;
    private SimWifiP2pSocket socket = null;
    private TextView mTextInput;
    private TextView mTextOutput;
    private SimWifiP2pBroadcastReceiver broadcastReceiver;


    private ListView listViewFoodServices;
    private ArrayList<MyDataListMain> arrayList = new ArrayList<>();
    private AdapterListViewMainFoodServices adapterFoodServices;


    public int checkedDistanceToCampusCounter = 0;
    private int counterDisplayFoodServiceInList = 0;
    private int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private boolean locationEnabled = false;


    //for downloading images on wifi
    // Creating DatabaseReference.
    DatabaseReference databaseReference;
    private StorageReference imagesRef;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();


    private List<String> services =new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //displayChosenCampus(currentCampus);

        //use getUserProfile() to get selected user. Returns user or null if user not selected
        //status = getUserProfile();

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        broadcastReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(broadcastReceiver, filter);

        //manager.requestPeers(channel, MainActivity.this);
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

        //For Broadcastreceiver
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(new WifiReceiver(), intentFilter);
    }

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
            getDistance(latLngAlameda);
            getDistance(latLngTaguspark);
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
                if (adapterView.getItemAtPosition(position).equals("Choose Campus")) {
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

        //Style and populate the spinner
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
        campuses.add(0, "Choose Campus");
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
            //services.clear();
            //arrayList.clear();
            OpeningHours openHours = new OpeningHours();
            List<String> foodServicesOpen ;
            foodServicesOpen= openHours.CafeteriasOpen(status,campus);
            List<String> foodServices= foodServicesOpen;
            services = foodServicesOpen;
            getDistanceValues(foodServices);
        }
        else if (campus.equals("Taguspark")){
            services.clear();
            arrayList.clear();
            displayMainFoodServicesList();
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
                if (foodService.equals("Main Building")) {
                    double latitude = 38.736574;
                    double longitude = -9.139561;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Civil Building")) {
                    double latitude = 38.7370555;
                    double longitude = -9.140102;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("North Tower")) {
                    double latitude = 38.7376027;
                    double longitude = -9.1386528;
                    LatLng latLngDest = new LatLng(latitude, longitude);
                    latLngs.add(latLngDest);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Mechanics Building II")) {
                    double latitude = 38.737145;
                    double longitude = -9.137595;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("AEIST Building")) {
                    double latitude = 38.736386;
                    double longitude = -9.136973;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Copy Section")) {
                    double latitude = 38.736346;
                    double longitude = -9.137839;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("South Tower")) {
                    double latitude = 38.7359943;
                    double longitude = -9.138551;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Mathematics Building")) {
                    double latitude = 38.735502;
                    double longitude = -9.139760;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Interdisciplinary Building")) {
                    double latitude = 38.736039;
                    double longitude = -9.140131;
                    LatLng latLngDest = new LatLng(latitude, longitude);

                    String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                    MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
                if (foodService.equals("Ground floor")) {
                    //TODO: find lat/lng
                }
                if (foodService.equals("Taguspark Campus Restaurant")) {
                    //TODO: find lat/lng
                }
            }
            else{
                if (foodService.equals("Main Building")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Civil Building")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                    arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                    counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("North Tower")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                    arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                    counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Mechanics Building II")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("AEIST Building")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Copy Section")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("South Tower")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Mathematics Building")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Interdisciplinary Building")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Ground floor")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
                        counterDisplayFoodServiceInList++;
                    }
                }
                if (foodService.equals("Taguspark Campus Restaurant")) {
                    if(counterDisplayFoodServiceInList<services.size()-1){
                        arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), "Location denied", 5));
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
            Toast.makeText(getApplicationContext(), "No user group selected. Please select user group under profile ", Toast.LENGTH_LONG).show();
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

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
        // TODO: something
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
                if(checkNetworkConnection() ){
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
                arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), duration, 5));
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
            }
            if (checkedDistanceToCampusCounter == 2){
                displayCampusName(currentCampus);
                displayListForChoosingCampus();
            }
        }
    }


    //check for wifi status of broadcast receiver
    private BroadcastReceiver wifiStateReceiver= new WifiReceiver();
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiStateReceiver);

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







/*
    private void wfifiDownloadImages(){
        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerViewImage);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        // Setting up Firebase image upload folder path in databaseReference.
        final String foodService= "Main Building";
        final String dishName="Pasta Carbonara";

        databaseReference = FirebaseDatabase.getInstance().getReference("Dishes/"+foodService+"/"+dishName + "/images");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                adapter = new RecyclerViewAdapter(getApplicationContext(), list, foodService, dishName);

                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });
    }
    */

/*
    private void cacheImages(){
        // Setting up Firebase image upload folder path in databaseReference.

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        imagesRef = storageReference.child("/images/Main Building/Pasta Carbonara"+"/"+"5063426b-be36-4f2d-8624-b8e141c21341");

        FutureTarget<File> future = Glide.with(this)
               .load(imagesRef)
                .downloadOnly(500, 500);
    }
*/


}