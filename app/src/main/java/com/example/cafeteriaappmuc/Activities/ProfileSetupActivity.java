package com.example.cafeteriaappmuc.Activities;


import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cafeteriaappmuc.Profile;
import com.example.cafeteriaappmuc.R;

//use get in other activityes to find restauratn and opening hours for the profile that is chosen
//TODO: create openinghours object?
public class ProfileSetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner users_spinner;
    private String selectedVal=null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);

        //Adding button to go back to main
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        users_spinner = (Spinner) findViewById(R.id.spinner_users);
        Button button = findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(selectedVal==null){
                    Toast.makeText(getApplicationContext(), "Please select user value from bar" ,Toast.LENGTH_SHORT).show();

                }

                else {
                    //Stored variables saved and updated every time "save Button" pushed
                    String key =getString(R.string.saved_profile_key);
                    insertData(key,selectedVal);

                    //retrive data(just for testing)
                    String testVal1 = retreiveData(key);
                    System.out.println("button clicked"+ testVal1);

                    //Store the same variable in profile object as Global Variable. Easy retrival for other classes.
                    Profile profileVariable = (Profile) getApplicationContext();
                    profileVariable.setProfile(testVal1);
                    Toast.makeText(getApplicationContext(), "User saved as: " + selectedVal, Toast.LENGTH_SHORT).show();
                    Log.i("OnClick", "Person saved" + selectedVal);
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

    }

    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
        if (users_spinner.getSelectedItem().toString().equals("Select user group")) {
            selectedVal = null;
        } else {
            selectedVal =users_spinner.getSelectedItem().toString();
            }

        }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){
        // Another interface callback
    }

    public void insertData(String key, String value) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String retreiveData(String key){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String defalutVal= getResources().getString(R.string.saved_profile_default_key);
        String profileVal = sharedPref.getString(getString(R.string.saved_profile_key),defalutVal);
        return profileVal;

    }


}