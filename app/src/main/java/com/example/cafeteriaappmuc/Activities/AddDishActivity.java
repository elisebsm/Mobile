package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cafeteriaappmuc.Dish;
import com.example.cafeteriaappmuc.DishType;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.R;
import com.example.cafeteriaappmuc.io.DishIO;

public class AddNewDishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_dish);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner typeSpinner = findViewById(R.id.newDishType); //lager dropdown meny
        ArrayAdapter<DishType> typeAdapter = new ArrayAdapter<>(this, //bruker også her et adapter for å fylle listen "Spinneren" med dishTypeValues
                android.R.layout.simple_spinner_item, DishType.values());
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    public void addNewDish(View view) {
        TextView nameTextView = findViewById(R.id.nameNewDish); //leser all dataen fra den nye dishen som er skrevet inn
        String newDishName = nameTextView.getText().toString();

        TextView priceTextView = findViewById(R.id.newDishPrice);
        String newDishPriceAsString = priceTextView.getText().toString();
        double newDishPrice = Double.parseDouble(newDishPriceAsString);

        TextView descriptionTextView = findViewById(R.id.newDishDescription);
        String newDishDescription = descriptionTextView.getText().toString();

        Spinner typeSpinner = findViewById(R.id.newDishType);
        DishType newDishType = (DishType) typeSpinner.getSelectedItem();  //gjør om til DishType (Caster!!)

        Dish newDish = new Dish(newDishName, newDishPrice, newDishDescription, newDishType); //lager ny dish med ny data
        String foodService = ((GlobalClass) this.getApplication()).getFoodService(); //henter rikitg foodservice (global class)
        DishIO.saveDish(foodService, newDish);

        Intent goBackToMenuIntent = new Intent (this, MenuDayActivity.class); //tilbake til meny
        startActivity(goBackToMenuIntent);
    }
}