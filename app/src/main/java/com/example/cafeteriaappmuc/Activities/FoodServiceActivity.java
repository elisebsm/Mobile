package com.example.cafeteriaappmuc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cafeteriaappmuc.DirectionsParser;
import com.example.cafeteriaappmuc.PermissionUtils;
import com.example.cafeteriaappmuc.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FoodServiceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String foodService = intent.getStringExtra("foodService");

        switch (foodService) {
            case "Main Building":
                 latitude = 38.736574;
                longitude = -9.139561;
                break;
            case "Civil Building":
                 latitude = 38.7370555;
                longitude = -9.140102;

                break;
            case "North Tower":
                 latitude = 38.7376027;
                longitude = -9.1386528;

                break;
            case "Mechanics Building II":
                 latitude = 38.737145;
                longitude = -9.137595;

                break;
            case "AEIST Building":
                 latitude = 38.736386;
                longitude = -9.136973;

                break;
            case "Copy Section":
                 latitude = 38.736346;
                longitude = -9.137839;

                break;
            case "South Tower":
                 latitude = 38.7359943;
                longitude = -9.138551;

                break;
            case "Mathematics Building":
                 latitude = 38.735502;
                longitude = -9.139760;

                break;
            case "Interdisciplinary Building":
                 latitude = 38.736039;
                longitude = -9.140131;

                break;
            case "Ground floor":

                break;
            case "Taguspark Campus Restaurant":

                break;
            default:

                break;
        }

        setContentView(R.layout.activity_food_service);
        TextView textViewFoodServiceName = findViewById(R.id.tvFoodServiceName);
        textViewFoodServiceName.setText(foodService);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ArrayList<LatLng> listPoints = new ArrayList<>();
        enableMyLocation();


        //TODO: Change back to current location when finished testing, google cant calculate route fro norway to portugal
       /* // Get current location
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double userLat = lastKnownLocation.getLatitude();
        double userLong = lastKnownLocation.getLongitude();
        Log.i("LOGLOCATION", "" + userLat + " , "+ userLong);*/

        double userLat = 38.737223;
        double userLong = -9.136487;
        LatLng latLngCurrentLoc = new LatLng(userLat, userLong);
        listPoints.add(latLngCurrentLoc);

        //sets zoom level
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentLoc, zoomLevel));

        LatLng latLngDestination = new LatLng(latitude,longitude);
        listPoints.add(latLngDestination);

        // create the url to get request from first marker to second marker from google map api
        String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
        FoodServiceActivity.TaskRequestDirections taskRequestDirections = new FoodServiceActivity.TaskRequestDirections();
        taskRequestDirections.execute(url);
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
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }


    private String getRequestUrl(LatLng origin, LatLng dest) {
        //value of origin
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        //value of destination
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // set value enable the sensor
        String sensor = "sensor=false";
        //mode for finding direction
        String mode = "mode=walking";
        //Build the full param
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode + "&key=AIzaSyB72zLudOuMncMtCOCIpwgMVvTBLFAfPI8";
        // output format
        String output = "json";
        // create url request
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
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
            while((line= bufferedReader.readLine())!= null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        }catch (IOException e ){
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return responseString;
    }


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
            FoodServiceActivity.TaskParser taskParser = new FoodServiceActivity.TaskParser();
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }


        protected void onPostExecute(List<List<HashMap<String,String>>> lists) {
            // Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lon = Double.parseDouble(Objects.requireNonNull(point.get("lon")));
                    points.add(new LatLng(lat,lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(8);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if(polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found.", Toast.LENGTH_LONG). show();
            }
        }
    }


}
