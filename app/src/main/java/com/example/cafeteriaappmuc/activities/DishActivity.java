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
        String dishName = intent.getStringExtra(MenuOfTheDayActivity.dishName);

        TextView textView = findViewById(R.id.dishName);
        textView.setText(dishName);
    }
}
