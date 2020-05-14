package com.example.cafeteriaappmuc.Activities;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafeteriaappmuc.DishType;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.OpeningHours;
import com.example.cafeteriaappmuc.R;

import java.util.HashMap;
import java.util.Map;


//use get in other activityes to find restauratn and opening hours for the profile that is chosen
//TODO: create openinghours object?
public class ProfileSetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner users_spinner;
    private String selectedSpinnerVal = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);


        users_spinner = (Spinner) findViewById(R.id.spinner_users);
        Button button = findViewById(R.id.save_button);
        final String key = getString(R.string.saved_profile_key);

        //checking if profile value is selected previously (Aka if something else than default value is selected)
        final String selectedUserProfile = retrieveData(key);
        if (selectedUserProfile != getString(R.string.saved_profile_default_key)) {
            TextView textViewProfile = (TextView) findViewById(R.id.prevSelectedDisplay);
            textViewProfile.setText(selectedUserProfile);

            // Toast.makeText(getApplicationContext(), "User previously saved as: " + selectedUserProfile, Toast.LENGTH_SHORT).show();
        }
        //saving profile value selected on the spinner(dropdown menu)
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedSpinnerVal == null) {
                    Toast.makeText(getApplicationContext(), "Please select user value from bar", Toast.LENGTH_SHORT).show();

                } else {

                    //Stored variables saved and updated every time "save Button" pushed
                    insertData(key, selectedSpinnerVal);
                    System.out.println(selectedSpinnerVal);
                    //Store the same variable in profile object as Global Variable. Easy retreival for other classes.
                    String sharedPrefProfile = retrieveData(key);
                    GlobalClass globalAssetsVariable = (GlobalClass) getApplicationContext();
                    globalAssetsVariable.setProfile(sharedPrefProfile);
                    Toast.makeText(getApplicationContext(), "User saved as: " + selectedSpinnerVal, Toast.LENGTH_SHORT).show();
                    Log.i("OnClick", "Person saved" + selectedSpinnerVal);

                }

            }

        });


        //populate spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.userprofile_arrays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        users_spinner.setAdapter(adapter);
        users_spinner.setOnItemSelectedListener(this);

        //UNDER HER diet constraints
        Button dietButton = findViewById(R.id.dietButton);

        //finne alle checkboxene

        Map<DishType,Boolean> dietPreferences = retrieveDietPreferences();
        CheckBox meatCheckBox = findViewById(R.id.meatCheckBox);
        meatCheckBox.setChecked(dietPreferences.get(DishType.Meat));
        CheckBox fishCheckBox = findViewById(R.id.fishCheckBox);
        fishCheckBox.setChecked(dietPreferences.get(DishType.Fish));
        CheckBox vegetarianCheckBox = findViewById(R.id.vegetarianCheckBox);
        vegetarianCheckBox.setChecked(dietPreferences.get(DishType.Vegetarian));
        CheckBox veganCheckBox = findViewById(R.id.veganCheckBox);
        veganCheckBox.setChecked(dietPreferences.get(DishType.Vegan));


        dietButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox meatCheckBox = findViewById(R.id.meatCheckBox);
                boolean meatValue = meatCheckBox.isChecked();

                CheckBox fishCheckBox = findViewById(R.id.fishCheckBox);
                boolean fishValue = fishCheckBox.isChecked();

                CheckBox vegetarianCheckBox = findViewById(R.id.vegetarianCheckBox);
                boolean vegetarianValue = vegetarianCheckBox.isChecked();

                CheckBox veganCheckBox = findViewById(R.id.veganCheckBox);
                boolean veganValue = veganCheckBox.isChecked();

                SharedPreferences sharedPrefDiet = getSharedPreferences("Diets", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefDiet.edit();
                //la til denne
                editor.clear();
                editor.putBoolean("Meat", meatValue);
                editor.putBoolean("Fish", fishValue);
                editor.putBoolean("Vegetarian", vegetarianValue);
                editor.putBoolean("Vegan", veganValue);
                editor.commit();

                Toast.makeText(getApplicationContext(), "Diet preferences are saved. ", Toast.LENGTH_SHORT).show();
            }

        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (users_spinner.getSelectedItem().toString().equals("Select user group")) {
            selectedSpinnerVal = null;
        } else {
            selectedSpinnerVal = users_spinner.getSelectedItem().toString();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void insertData(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //la til denne
        editor.clear();
        editor.putString(key, value);
        editor.commit();
    }

    public String retrieveData(String key) {
        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String defaultVal = getResources().getString(R.string.saved_profile_default_key);
        String profileVal = sharedPref.getString(key, defaultVal);
        return profileVal;

    }

    public Map<DishType, Boolean> retrieveDietPreferences(){
        SharedPreferences sharedPref = getSharedPreferences("Diets", Context.MODE_PRIVATE);
        boolean meatValue = sharedPref.getBoolean("Meat",true);
        boolean fishValue = sharedPref.getBoolean("Fish",true);
        boolean vegetarianValue = sharedPref.getBoolean("Vegetarian",true);
        boolean veganValue = sharedPref.getBoolean("Vegan",true);

        Map<DishType, Boolean> dietMap = new HashMap<>();
        dietMap.put(DishType.Meat, meatValue);
        dietMap.put(DishType.Fish, fishValue);
        dietMap.put(DishType.Vegetarian, vegetarianValue);
        dietMap.put(DishType.Vegan, veganValue);
        return dietMap;
    }

}