package com.example.cafeteriaappmuc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cafeteriaappmuc.DirectionsParser;
import com.example.cafeteriaappmuc.Dish;
import com.example.cafeteriaappmuc.DishType;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.PermissionUtils;
import com.example.cafeteriaappmuc.R;
import com.example.cafeteriaappmuc.io.DishIO;
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
import java.util.Map;
import java.util.Objects;

public class FoodServiceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private double latitude;
    private double longitude;
    private String foodService;
    static String DISH_NAME = "DISH_NAME";
    static String DISH_PRICE = "DISH_PRICE";
    static String DISH_DESCRIPTION = "DISH_DESCRIPTION";

    //TODO: add opening hours
    //TODO: show walking time, update every second minute or so??

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        foodService = intent.getStringExtra("foodService");
        ((GlobalClass) this.getApplication()).setFoodService(foodService);

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

        //fra menuoftheday activity
        //super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_menu_of_the_day);

        //kan fjernes: String foodService = ((GlobalClass) this.getApplication()).getFoodService();

        DishIO.getAllDishes(foodService, new DishIO.FirebaseCallback() { //henter alle disher fra DishIO
            @Override
            public void onCallback(List<Dish> list) { //Bruker callback og asynkron kode for å være sikker på å få alle elementene vi trenger før vi kjører koden
                final Dish[] allDishes = list.toArray(new Dish[list.size()]);

                Map<DishType, Boolean> preferences = retrieveDietPreferences();
                List<Dish> filteredDishes = new ArrayList<>();
                for (Dish dish : list) {
                    if (preferences.get(dish.type)) {
                        filteredDishes.add(dish);
                    }

                }

                final ArrayAdapter filterAdapter = new ArrayAdapter<Dish>(getApplicationContext(),
                        R.layout.dish_list_element, filteredDishes);

                final ArrayAdapter allAdapter = new ArrayAdapter<Dish>(getApplicationContext(),
                        R.layout.dish_list_element, allDishes);

                final ListView listView = (ListView) findViewById(R.id.dishList);

                CheckBox showHiddenBox = findViewById(R.id.showHiddenDishes);
                if(list.size() == filteredDishes.size()){
                    showHiddenBox.setVisibility(View.GONE);
                }

                showHiddenBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            listView.setAdapter(allAdapter);
                        } else {
                            listView.setAdapter(filterAdapter);
                        }

                    }
                });


                listView.setAdapter(filterAdapter); //bruker adapter for å fylle en liste
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //lager en listner på "On click" for all dishene, som sender deg til riktig dish
                        Dish selectedDish = allDishes[position];

                        Intent intent = new Intent(view.getContext(), DishActivity.class); //bruker intent og extras til å sende info om dishene til dishActivity
                        intent.putExtra(DISH_NAME, selectedDish.name);
                        intent.putExtra(DISH_PRICE, Double.toString(selectedDish.price));
                        intent.putExtra(DISH_DESCRIPTION, selectedDish.description);
                        startActivity(intent);
                    }
                });
            }
        });
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
        float zoomLevel = 19.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentLoc, zoomLevel));

        LatLng latLngDestination = new LatLng(latitude, longitude);
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
        //Build the full param TODO: make the key work without hard coding it.
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


        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            // Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lon = Double.parseDouble(Objects.requireNonNull(point.get("lon")));
                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(8);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToAddNewDish(View view) {
        Intent intent = new Intent(this, AddNewDishActivity.class);
        startActivity(intent);
    }

    public Map<DishType, Boolean> retrieveDietPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("Diets", Context.MODE_PRIVATE);
        boolean meatValue = sharedPref.getBoolean("Meat", true);
        boolean fishValue = sharedPref.getBoolean("Fish", true);
        boolean vegetarianValue = sharedPref.getBoolean("Vegetarian", true);
        boolean veganValue = sharedPref.getBoolean("Vegan", true);

        Map<DishType, Boolean> dietMap = new HashMap<>();
        dietMap.put(DishType.Meat, meatValue);
        dietMap.put(DishType.Fish, fishValue);
        dietMap.put(DishType.Vegetarian, vegetarianValue);
        dietMap.put(DishType.Vegan, veganValue);
        return dietMap;
    }
}