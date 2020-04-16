package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
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
import com.example.cafeteriaappmuc.MyDataListMain;
import com.example.cafeteriaappmuc.R;

import com.google.android.gms.maps.model.LatLng;

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
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Serializable {

    private List<String> campusesAll = new ArrayList<>();

    //TODO: set current campus based on profile
    private String currentCampus = " ";
    private String status;
    //final Button button = findViewById(R.id.profile_button);


    ListView listViewFoodServices;
    ArrayList<MyDataListMain> arrayList = new ArrayList<>();
    AdapterListViewMainFoodServices adapterFoodServices;


    private int counterDisplayFoodServiceInList = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayChosenCampus(currentCampus);

        //use getUserProfile() to get selected user. Returns user or null if user not selected
        status = getUserProfile();

        Spinner spinnerListCampuses = findViewById(R.id.spinnerListOfCampus);
        campusesAll.add("Alameda");
        campusesAll.add("Taguspark");
        List<String> campuses = removeCurrentCampusFromList(currentCampus);

        // Style and populate the spinner
        ArrayAdapter<String> campusAdapter;
        campusAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, campuses);

        // Dropdown layout style
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapterFoodServices to spinner
        spinnerListCampuses.setAdapter(campusAdapter);
        spinnerListCampuses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (adapterView.getItemAtPosition(position).equals("Choose Campus")) {
                    // do nothing
                } else {

                    counterDisplayFoodServiceInList = 0;

                    displayChosenCampus(adapterView.getItemAtPosition(position).toString());
                    removeCurrentCampusFromList(currentCampus);
                    updateSpinner(adapterView.getItemAtPosition(position).toString());
                    displayDiningOptions(status, currentCampus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        displayMainFoodServicesList();
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


    private void displayChosenCampus(String campusName) {
        currentCampus = campusName;
        TextView textViewMainCurrentCampusSet = findViewById(R.id.textViewMainCurrentCampus);
        textViewMainCurrentCampusSet.setText(campusName);
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

    // int numberofservices = 0;
    List<String> services = new ArrayList<>();

    /**
     * Shows dining places based on status
     */
    // TODO: connect this to profile, add specification for status
    private void displayDiningOptions(String status, String campus) {
        //List<String> foodServicesTaguspark = Arrays.asList("Ground floor", "Taguspark Campus Restaurant", "Floor -1");
        //List<String> foodServicesAlameda = Arrays.asList("Main Building", "Civil Building", "North Tower", "Mechanics Building II", "AEIST Building", " Copy Section", "South Tower", "Mathematics Building", "Interdisciplinary Building");

        switch (status) {
            case "Student":
                if (campus.equals("Alameda")) {
                    //TODO: add accurate food services according to status
                    List<String> foodServices = Arrays.asList("Main Building", "Civil Building", "North Tower", "Mechanics Building II", "AEIST Building", " Copy Section");
                    services = Arrays.asList("Main Building", "Civil Building", "North Tower", "Mechanics Building II", "AEIST Building", " Copy Section");
                    //numberofservices += foodServices.size();
                    getDistanceValues(foodServices);

                } else {
                    //TODO append foddservices to services
                    arrayList.clear();

                    displayMainFoodServicesList();
                }
                break;
            case "Professor":
                if (campus.equals("Alameda")) {
                    arrayList.clear();
                } else {
                    arrayList.clear();
                }
                break;
            case "Researcher":
                if (campus.equals("Alameda")) {
                    //TODO: add food services for researcher
                    arrayList.add(new MyDataListMain("Ground floor", "5", 5));
                    arrayList.clear();
                } else {
                    arrayList.clear();
                }
                break;
            case "Staff":
                if (campus.equals("Alameda")) {
                    //TODO: add food services for staff
                    arrayList.add(new MyDataListMain("Taguspark Campus Restaurant", "6", 3));
                    arrayList.clear();
                } else {
                    arrayList.clear();
                }
                break;
        }
    }


    // TODO: make the key work without hard coding it.
    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        return "https://maps.googleapis.com/maps/api/directions/json?" + str_org + "&" + str_dest + "&sensor=false&mode=walking&key=AIzaSyB72zLudOuMncMtCOCIpwgMVvTBLFAfPI8";
    }

// BRUKES FOR Å LAGE LISTEN OVER SPISESTEDER
    // private ArrayList<String> servicesToBeDisplayed = new ArrayList<>();


    //private List<String> walkDistances  = new ArrayList<>();
    private void getDistanceValues(List<String> foodServices) {

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
            if (foodService.equals("Main Building")) {
                double latitude = 38.736574;
                double longitude = -9.139561;
                LatLng latLngDest = new LatLng(latitude, longitude);
                latLngs.add(latLngDest);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);

                // servicesToBeDisplayed.add("Main Building");


            }
            if (foodService.equals("Civil Building")) {
                double latitude = 38.7370555;
                double longitude = -9.140102;
                LatLng latLngDest = new LatLng(latitude, longitude);
                latLngs.add(latLngDest);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);
                //servicesToBeDisplayed.add("Civil Building");
            }
            if (foodService.equals("North Tower")) {
                double latitude = 38.7376027;
                double longitude = -9.1386528;
                LatLng latLngDest = new LatLng(latitude, longitude);
                latLngs.add(latLngDest);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);
                //servicesToBeDisplayed.add("North Tower");

            }
            if (foodService.equals("Mechanics Building II")) {
                double latitude = 38.737145;
                double longitude = -9.137595;
                LatLng latLngDest = new LatLng(latitude, longitude);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);
                //servicesToBeDisplayed.add("Mechanics Building II");

            }
            if (foodService.equals("AEIST Building")) {
                double latitude = 38.736386;
                double longitude = -9.136973;
                LatLng latLngDest = new LatLng(latitude, longitude);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);
                //servicesToBeDisplayed.add("AEIST Building");
            }
            if (foodService.equals("Copy Section")) {
                double latitude = 38.736346;
                double longitude = -9.137839;
                LatLng latLngDest = new LatLng(latitude, longitude);

                String url = getRequestUrl(latLngCurrentLoc, latLngDest);
                MainActivity.TaskRequestDirections taskRequestDirections = new MainActivity.TaskRequestDirections();
                taskRequestDirections.execute(url);
                //servicesToBeDisplayed.add("Copy Section");
            }
            if (foodService.equals("South Tower")) {
                double latitude = 38.7359943;
                double longitude = -9.138551;
                LatLng latLngDest = new LatLng(latitude, longitude);
            }
            if (foodService.equals("Mathematics Building")) {
                double latitude = 38.735502;
                double longitude = -9.139760;
                LatLng latLngDest = new LatLng(latitude, longitude);
            }
            if (foodService.equals("Interdisciplinary Building")) {
                double latitude = 38.736039;
                double longitude = -9.140131;
                LatLng latLngDest = new LatLng(latitude, longitude);
            }
            if (foodService.equals("Ground floor")) {
                //TODO: find lat/lng
            }
            if (foodService.equals("Taguspark Campus Restaurant")) {
                //TODO: find lat/lng
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
    //show Upload image activity. Only for testing
    private void showUploadImageActivity() {
        Intent intentUploadImageActivity = new Intent(this, UploadImageActivity.class);
        startActivity(intentUploadImageActivity);
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


//TODO : maybe remove profile class and only use shared preferences
    //get user profile selected in profile
    private String getUserProfile() {

        final String key =getString(R.string.saved_profile_key);
        final String defValue = getString(R.string.saved_profile_default_key);
        SharedPreferences sharedPref = getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        String selectedUserProfile = sharedPref.getString(key, defValue);
        //check if profile is saved as something else than default
        if (selectedUserProfile==getString(R.string.saved_profile_default_key)){
            Toast.makeText(getApplicationContext(), "No user selected. Please select user profile ", Toast.LENGTH_SHORT).show();
            return null;
        }
        else {
            //profileVariable.setProfile(selectedUserProfile);
            Toast.makeText(getApplicationContext(), "User saved as: " + selectedUserProfile , Toast.LENGTH_SHORT).show();
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


    /**
     * TaskRequestDirection and TarskParser are used for the AsyncTask to get time to walk
     * TODO: put them in their own class??
     */
    // creates AsyncTask to call request Direction
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

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
            // String foodService = "";

            arrayList.add(new MyDataListMain(services.get(counterDisplayFoodServiceInList), duration, 5));
            displayMainFoodServicesList();
            counterDisplayFoodServiceInList++;


        }
    }

}


