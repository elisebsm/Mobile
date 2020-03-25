package com.example.cafeteriaappmuc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cafeteriaappmuc.R;

public class DishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);

        Intent intent = getIntent();
        String dishName = intent.getStringExtra(MenuOfTheDayActivity.DISH_NAME);
        String dishPrice = intent.getStringExtra(MenuOfTheDayActivity.DISH_PRICE);
        String dishDescription = intent.getStringExtra(MenuOfTheDayActivity.DISH_DESCRIPTION);


        TextView nameTextView = findViewById(R.id.dishName);
        nameTextView.setText(dishName);

        TextView priceTextView = findViewById(R.id.dishPrice);
        priceTextView.setText(dishPrice + " Euros");

        TextView descriptionTextView = findViewById(R.id.dishDescription);
        descriptionTextView.setText(dishDescription);


    }
}
