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
    private String selectedSpinnerVal=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);

        //Adding button to go back to main
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        users_spinner = (Spinner) findViewById(R.id.spinner_users);
        Button button = findViewById(R.id.save_button);
        final String key =getString(R.string.saved_profile_key);

        //checking if profile value is selected previously (Aka if something else than default value is selected)
        String selectedUserProfile = retrieveData(key);
        if (selectedUserProfile!=getString(R.string.saved_profile_default_key)) {
            //TODO: DISPLAY previously selected profile setting
            //quickfix
            Toast.makeText(getApplicationContext(), "User previously saved as: " + selectedUserProfile, Toast.LENGTH_SHORT).show();
        }
        //saving profile value selected on the spinner(dropdown menu)
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedSpinnerVal == null) {
                    Toast.makeText(getApplicationContext(), "Please select user value from bar", Toast.LENGTH_SHORT).show();

                } else {
                    //Stored variables saved and updated every time "save Button" pushed
                    insertData(key, selectedSpinnerVal);

                    //Store the same variable in profile object as Global Variable. Easy retreival for other classes.
                    String sharedPrefProfile = retrieveData(key);
                    Profile profileVariable = (Profile) getApplicationContext();
                    profileVariable.setProfile(sharedPrefProfile);
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

    }

    @Override
    public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
        if (users_spinner.getSelectedItem().toString().equals("Select user group")) {
            selectedSpinnerVal = null;
        } else {
            selectedSpinnerVal =users_spinner.getSelectedItem().toString();
            }

        }

    @Override
    public void onNothingSelected (AdapterView < ? > parent){
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

    public String retrieveData(String key){
        SharedPreferences sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String defaultVal= getResources().getString(R.string.saved_profile_default_key);
        String profileVal = sharedPref.getString(getString(R.string.saved_profile_key),defaultVal);
        return profileVal;

    }

}