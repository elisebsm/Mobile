package com.example.cafeteriaappmuc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cafeteriaappmuc.Profile;
import com.example.cafeteriaappmuc.R;

public class ProfileSetupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner users_spinner;
    final Button button = findViewById(R.id.save_button);


    // addListenerOnButton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);
        populateSpinner();
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startActivity(new Intent(ProfileSetupActivity.this, MainActivity.class));
            }
        });

    }


    //populate spinner with user choises from userprofile_arrays
    public void populateSpinner() {
        users_spinner     = (Spinner) findViewById(R.id.users_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.userprofile_arrays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        users_spinner.setAdapter(adapter);


    }

    //responding to user actions
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(users_spinner.getItemAtPosition(position).equals("Select user group")){
           // do nothing
        }
        else{
            // An item was selected. Retrieve the selected item and pass it to global variable
            users_spinner.setOnItemSelectedListener(this);
            ((Profile) this.getApplication()).setProfile(users_spinner.getItemAtPosition(position).toString());

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    public void onButtonClicked(View view){

    }




    //bruke get i andre aktiviteter typ finne restaurant og åpningsider for den profilen

}