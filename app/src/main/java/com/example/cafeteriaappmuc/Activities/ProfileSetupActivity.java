package com.example.cafeteriaappmuc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cafeteriaappmuc.Profile;
import com.example.cafeteriaappmuc.R;


//bruke get i andre aktiviteter typ finne restaurant og åpningsider for den profilen som blir valgt
public class ProfileSetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner users_spinner;
    private String selectedVal=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);
        //legger til tilbakeknapp
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        users_spinner = (Spinner) findViewById(R.id.spinner_users);
        Button button = findViewById(R.id.save_button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(selectedVal==null){
                    Toast.makeText(getApplicationContext(), "Please select user value from bar" ,Toast.LENGTH_SHORT).show();

                }

                else {
                    System.out.println("button clicked");
                    Profile profileVariable = (Profile) getApplicationContext();
                    profileVariable.setProfile(selectedVal);
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



}