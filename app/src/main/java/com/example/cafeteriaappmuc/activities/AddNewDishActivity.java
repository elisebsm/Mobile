package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.cafeteriaappmuc.Dish;
import com.example.cafeteriaappmuc.R;
import com.example.cafeteriaappmuc.io.LocalDishIO;

public class AddNewDishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_dish);
    }

    public void addNewDish(View view) {
        TextView nameTextView = findViewById(R.id.nameNewDish);
        String newDishName = nameTextView.getText().toString();

        TextView priceTextView = findViewById(R.id.newDishPrice);
        String newDishPriceAsString = priceTextView.getText().toString();
        double newDishPrice = Double.parseDouble(newDishPriceAsString);

        TextView descriptionTextView = findViewById(R.id.newDishDescription);
        String newDishDescription = descriptionTextView.getText().toString();

        Dish newDish = new Dish(newDishName, newDishPrice, newDishDescription);
        LocalDishIO.saveDish(newDish);

        Intent goBackToMenuIntent = new Intent (this, MenuOfTheDayActivity.class);
        startActivity(goBackToMenuIntent);
    }
}
