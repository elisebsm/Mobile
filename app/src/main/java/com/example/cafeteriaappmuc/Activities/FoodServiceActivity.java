package com.example.cafeteriaappmuc.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
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
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FoodServiceActivity extends AppCompatActivity {

    private GoogleMap mMap;
    private GoogleMap mMapDirection;
    int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private LatLng latLngDestination;
    private String foodService;
    static String DISH_NAME = "DISH_NAME";
    static String DISH_PRICE = "DISH_PRICE";
    static String DISH_DESCRIPTION = "DISH_DESCRIPTION";
    private LatLng latLngOrig;
    private List<String> dishNames = new ArrayList<>();


    //TODO: show walking time, update every second minute or so??

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        foodService = intent.getStringExtra("foodService");
        ((GlobalClass) this.getApplication()).setFoodService(foodService);

        if (foodService != null) {
            double latitudeDest;
            double longitudeDest;
            switch (foodService) {
                case "Central Bar":
                    latitudeDest = 38.736606;
                    longitudeDest = -9.139532;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Civil Bar":
                    latitudeDest = 38.736988;
                    longitudeDest = -9.139955;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Civil Cafeteria":
                    latitudeDest = 38.737650;
                    longitudeDest = -9.140384;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Sena Pastry Shop":
                    latitudeDest = 38.737677;
                    longitudeDest = -9.138672;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Mechy Bar":
                    latitudeDest = 38.737247;
                    longitudeDest = -9.137434;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "AEIST Bar":
                    latitudeDest = 38.736542;
                    longitudeDest = -9.137226;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "AEIST Esplanade":
                    latitudeDest = 38.736318;
                    longitudeDest = -9.137820;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Chemy Bar":
                    latitudeDest = 38.736240;
                    longitudeDest = -9.138302;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "SAS Cafeteria":
                    latitudeDest = 38.736571;
                    longitudeDest = -9.137036;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Math Cafeteria":
                    latitudeDest = 38.735508;
                    longitudeDest = -9.139645;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Complex Bar":
                    latitudeDest = 38.736050;
                    longitudeDest = -9.140156;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Tagus Cafeteria":
                    latitudeDest = 38.737802;
                    longitudeDest = -9.303223;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Red Bar":
                    latitudeDest = 38.736546;
                    longitudeDest = -9.302207;

                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "Green Bar":
                    latitudeDest = 38.738004;
                    longitudeDest = -9.303058;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "CTN Cafeteria":
                    latitudeDest = 38.812522;
                    longitudeDest = -9.093773;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;
                case "CTN Bar":
                    latitudeDest = 38.812522;
                    longitudeDest = -9.093773;
                    latLngDestination = new LatLng(latitudeDest, longitudeDest);
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + foodService);
            }
        }

        setContentView(R.layout.activity_food_service);
        TextView textViewFoodServiceName = findViewById(R.id.tvFoodServiceName);
        textViewFoodServiceName.setText(foodService);

        TextView textViewOpeningHours = findViewById(R.id.tvOpeningHours);
        String openingHours = getResources().getString(R.string.tv_opening_hours) + getOpeningHourForFoodService(foodService);
        textViewOpeningHours.setText(openingHours);

        //listen for share button click
        Button shareBtn = findViewById(R.id.shareFoodServiceBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSocialMedia();
            }
        });
        setWalkingTime();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(onMapReadyCallback1());
        }

        SupportMapFragment mapFragmenttwo = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMapDirection);

        if (mapFragmenttwo != null) {
            mapFragmenttwo.getMapAsync(onMapReadyCallback2());
        }


        DishIO.getAllDishes(foodService, new DishIO.FirebaseCallback() { //henter alle disher fra DishIO
            @Override
            public void onCallback(List<Dish> list) { //Bruker callback og asynkron kode for å være sikker på å få alle elementene vi trenger før vi kjører koden
                //get list of dishnames (that is not final) in order to use in socialmedia
                final Dish[] allDishes = list.toArray(new Dish[list.size()]);

                for (int i = 0; i < list.size(); i++) {
                    Dish selectedDish = allDishes[i];
                    String dishName = selectedDish.name;
                    dishNames.add(dishName);
                }
                if (allDishes.length == 0) {
                    findViewById(R.id.tvNoRegisteredDishes).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.tvNoRegisteredDishes).setVisibility(View.GONE);
                }

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

                final ListView listView = findViewById(R.id.dishList);

                CheckBox showHiddenBox = findViewById(R.id.showHiddenDishes);
                if (list.size() != filteredDishes.size()) {
                    showHiddenBox.setVisibility(View.VISIBLE);
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

        Button btnCloseMap = findViewById(R.id.btnCloseMap);
        btnCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.linearlayoutMapFragDirect).setVisibility(View.GONE);
                findViewById(R.id.constraintLayoutFoodserviceActiv).setVisibility(View.VISIBLE);
            }
        });

    }

    public OnMapReadyCallback onMapReadyCallback1() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (checkNetworkConnection()) {
                    mMap = googleMap;
                    enableMyLocation();
                    mMap.addMarker(new MarkerOptions().position(latLngDestination));
                    float zoomLevel = 16.5f;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngDestination, zoomLevel));
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            findViewById(R.id.linearlayoutMapFragDirect).setVisibility(View.VISIBLE);
                            findViewById(R.id.constraintLayoutFoodserviceActiv).setVisibility(View.GONE);
                        }
                    });
                }
            }
        };
    }


    private LatLng getCurrentLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        assert locationManager != null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //do nothing
        }
        android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        locationManager.requestLocationUpdates(locationProvider, (long) 0.5, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setWalkingTime();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // do nothing
            }

            @Override
            public void onProviderEnabled(String provider) {
                // do nothing
            }

            @Override
            public void onProviderDisabled(String provider) {
                // do nothing
            }
        });
        assert lastKnownLocation != null;
        userLat = lastKnownLocation.getLatitude();
        userLong = lastKnownLocation.getLongitude();
        latLngOrig = new LatLng(userLat, userLong);
        return latLngOrig;

    }


    public OnMapReadyCallback onMapReadyCallback2(){
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if(checkNetworkConnection()){
                    mMapDirection = googleMap;
                    mMapDirection.setMyLocationEnabled(true);
                    ArrayList<LatLng> listPoints = new ArrayList<>();
                    latLngOrig = new LatLng(userLat, userLong);
                    listPoints.add(latLngOrig);
                    float zoomLevel = 16.0f;
                    mMapDirection.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOrig, zoomLevel));
                    mMapDirection.addMarker(new MarkerOptions().position(latLngDestination));
                    listPoints.add(latLngDestination);

                    // create the url to get request from first marker to second marker from google map api
                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
                    FoodServiceActivity.TaskRequestDirections taskRequestDirections = new FoodServiceActivity.TaskRequestDirections();
                    taskRequestDirections.execute(url);
                }
            }
        };
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
            PermissionUtils.requestPermissionFood(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }


    //TODO: make the key work without hard coding it.
    private String getRequestUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        return "https://maps.googleapis.com/maps/api/directions/json?" + str_org + "&" + str_dest + "&sensor=false&mode=walking&key=AIzaSyB72zLudOuMncMtCOCIpwgMVvTBLFAfPI8";
    }

    private double userLat;
    private double userLong;
    private void setWalkingTime(){
        //checking for internet connection
        if(checkNetworkConnection()) {
            LatLng latLngCurrentLoc = getCurrentLocation();

            String url = getRequestUrl(latLngCurrentLoc, latLngDestination);
            FoodServiceActivity.TaskRequestDistanceToCampuses taskRequestDistanceToCampuses = new FoodServiceActivity.TaskRequestDistanceToCampuses();
            taskRequestDistanceToCampuses.execute(url);
        } else{
            TextView textViewWalkingTime = findViewById(R.id.tvEstimatedWalkingTime);
            textViewWalkingTime.setText(getString(R.string.tv_est_time_to_walk_not_available));
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
     * TaskRequestDistanceToCampuses and TarskParserDistance are used for the AsyncTask to
     * get distance to Taguspark and Alameda from current location
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
            FoodServiceActivity.TaskParserDistance taskParserDistance = new FoodServiceActivity.TaskParserDistance();
            taskParserDistance.execute(s);
        }
    }


    public class TaskParserDistance extends AsyncTask<String, Void, String> {

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

        //@RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String duration) {
            TextView textViewWalkingTime = findViewById(R.id.tvEstimatedWalkingTime);
            String timeToWalk = getString(R.string.tv_estimated_time_to_walk) + duration;
            textViewWalkingTime.setText(timeToWalk);
        }
    }

    // creates AsyncTask to call request Direction
    @SuppressLint("StaticFieldLeak")
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

    @SuppressLint("StaticFieldLeak")
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

            if(polylineOptions != null){
                mMapDirection.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goToAddNewDish(View view) {
        Intent intent = new Intent(this, AddNewDishActivity.class);
        startActivity(intent);
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

    private void shareSocialMedia(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Hi!");
        StringBuilder builder = new StringBuilder();

        if (dishNames.size()==0){
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Come and sit with me at the " +foodService+". "+"They do not have food so bring your own!");

        }
        else if(dishNames.size()<=1){
            for (String value : dishNames) {
                builder.append(value+" ");
            }
            String dishesToShare = builder.toString();
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Come and eat with me at the " +foodService+". "+"They have "+dishesToShare+"on the menu." );
        }
        else{
            //parse string and remove last element of list
            int len = dishNames.size()-1;
            String lastElement= dishNames.get(len);

            dishNames.remove(len);
            for (String value : dishNames) {
                builder.append(value+", ");
            }
            String dishesToShare = builder.toString();
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Come and eat with me at the " +foodService+". "+"They have "+dishesToShare+" and "+
                    lastElement+" on the menu." );

        }

        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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

    protected String getOpeningHourForFoodService(String foodservice){
        List<String> openingHoursList = Arrays.asList("9:00-17:00", "9:00-17:00", "12:00-15:00", "8:00-19:00", "9:00-17:00", "9:00-17:00", "9:00-17:00", "9:00-17:00",
                "9:00-21:00", "12:00-15:00", "8:00-22:00", "7:00-19:00", "12:00-14:00", "8:30-12:00, 15:30-16:30");
        List<String> foodservices = Arrays.asList("Central Bar", "Civil Bar", "Civil Cafeteria", "Sena Pastry Shop", "Mechy Bar", "AEIST Bar", "AEIST Esplanade", "Chemy Bar",
                "SAS Cafeteria", "Tagus Cafeteria", "Red Bar", "Green Bar", "CTN Cafeteria", "CTN Bar");
        if (foodservice.equals("Math Cafeteria")){
            if(getUserProfile().equals("Professors")||getUserProfile().equals("Researchers")||getUserProfile().equals("Staff")){
                return("12:00-15:00");
            } else {
                return "13:30-15:00";
            }
        } else if ( foodservice.equals("Complex Bar")){
            if(getUserProfile().equals("Professors")||getUserProfile().equals("Researchers")||getUserProfile().equals("Staff")){
                return"12:00-15:00";
            } else {
                return "9:00-12:00, 14:00-17:00 ";
            }
        }

        for (String cafeteria: foodservices){
            if (cafeteria.equals(foodservice)){
                int index = foodservices.indexOf(cafeteria);
                return openingHoursList.get(index);
            }
        }
        return "Not found.";
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
}