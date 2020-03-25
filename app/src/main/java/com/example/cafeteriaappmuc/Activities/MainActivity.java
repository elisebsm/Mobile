package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cafeteriaappmuc.Adapter.AdapterListViewMainFoodServices;
import com.example.cafeteriaappmuc.MyDataListMain;
import com.example.cafeteriaappmuc.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> campusesAll = new ArrayList<>();

    //TODO: set current campus based on profile
    private String currentCampus = " ";
    private String status;
    //final Button button = findViewById(R.id.profile_button);


    ListView listViewFoodServices;
    ArrayList<MyDataListMain> arrayList = new ArrayList<>();
    AdapterListViewMainFoodServices adapterFoodServices;

    //TODO: get time to walk
    //Time to walk value
    private ArrayList<String> timeToWalkList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayChosenCampus(currentCampus);

        // TODO: set status based on profile
        status = "Student";

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
                if(adapterView.getItemAtPosition(position).equals("Choose Campus")){
                    // do nothing
                }else {
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
        switch (item.getItemId()) {
            case R.id.item1:
                showProfileSetup();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    private void updateSpinner(String chosenCampus){
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


    private void displayChosenCampus(String campusName){
        currentCampus = campusName;
        TextView textViewMainCurrentCampusSet = findViewById(R.id.textViewMainCurrentCampus);
        textViewMainCurrentCampusSet.setText(campusName);
//       TODO: show campus from what the user chose for the main campus
    }


    private List<String> removeCurrentCampusFromList (String currentCampus) {
        int numberOfCampuses = campusesAll.size();
        List<String> campuses = new ArrayList<>();
        campuses.add(0, "Choose Campus");
        for (int i = 0; i < numberOfCampuses ; i++) {
            if (!campusesAll.get(i).equals(currentCampus)) {
                campuses.add(campusesAll.get(i));
            }
        }
        return campuses;
    }


    //Shows dining places based on status
    // TODO: connect this to profile, add specification for status
    private void displayDiningOptions(String status, String campus){
        //List<String> foodServicesAlameda = Arrays.asList("Main Building", "Civil Building", "North Tower", "Mechanics Building II", "AEIST Building", " Copy Section", "South Tower", "Mathematics Building", "Interdisciplinary Building");
        //List<String> foodServicesTaguspark = Arrays.asList("Ground floor", "Taguspark Campus Restaurant", "Floor -1");

        switch (status) {
            case "Student":
                if (campus.equals("Alameda")) {
                    arrayList.clear();
                    arrayList.add(new MyDataListMain("Main Building", 4, 5));
                    arrayList.add(new MyDataListMain("Civil Building", 12, 7));
                    displayMainFoodServicesList();
                } else {
                    arrayList.clear();
                    arrayList.add(new MyDataListMain("Ground floor", 33, 5));
                    arrayList.add(new MyDataListMain("Taguspark Campus Restaurant", 4, 3));
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
                    arrayList.add(new MyDataListMain("Ground floor", 5, 5));
                    arrayList.clear();
                } else {
                    arrayList.clear();
                }
                break;
            case "Staff":
                if (campus.equals("Alameda")) {
                    //TODO: add food services for staff
                    arrayList.add(new MyDataListMain("Taguspark Campus Restaurant", 6, 3));
                    arrayList.clear();
                } else {
                    arrayList.clear();
                }
                break;
        }
    }


    private void displayMainFoodServicesList(){
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

    private void showFoodService(String foodService){
        Intent intentFoodService = new Intent(this, FoodServiceActivity.class);
        intentFoodService.putExtra("foodService", foodService);
        startActivity(intentFoodService);
    }

    //starter profile setup activity
    private void showProfileSetup(){
        Intent intentProfileSetup = new Intent(this, ProfileSetupActivity.class);

        startActivity(intentProfileSetup);
    }




}
