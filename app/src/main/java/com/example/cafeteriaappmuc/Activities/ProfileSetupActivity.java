package com.example.cafeteriaappmuc.Activities;

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
    private Object user;
    private Spinner users_spinner;
    private Object pos;
    final Button button = findViewById(R.id.save_button);


    // addListenerOnButton();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setup);
        populateSpinner();
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                // TODO: FINNE NOE HER
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
        // An item was selected. You can retrieve the selected item using:
        pos = parent.getItemAtPosition(position);
        users_spinner     = (Spinner) findViewById(R.id.users_spinner);

        users_spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    public void onButtonClicked(View view){
        ((Profile) this.getApplication()).setProfile(pos);
    }




    //bruke get i andre aktiviteter typ finne restaurant og åpningsider for den profilen

}