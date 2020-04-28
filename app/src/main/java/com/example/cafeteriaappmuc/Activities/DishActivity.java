package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;

import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.R;



public class DishActivity extends AppCompatActivity {

    private Button uploadImagebtn,showImagesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        Intent intent = getIntent();
        final String dishName = intent.getStringExtra(MenuOfTheDayActivity.DISH_NAME); //henter "extra"ene som er sendt med intent, henter dish navnet
        String dishPrice = intent.getStringExtra(MenuOfTheDayActivity.DISH_PRICE);
        String dishDescription = intent.getStringExtra(MenuOfTheDayActivity.DISH_DESCRIPTION);

        TextView nameTextView = findViewById(R.id.dishName);
        nameTextView.setText(dishName);

        TextView priceTextView = findViewById(R.id.dishPrice); //id i xml-en der prisen faktisk skal stå
        priceTextView.setText(dishPrice + " Euros");

        ((GlobalClass) this.getApplication()).setDishName(dishName);

        TextView descriptionTextView = findViewById(R.id.dishDescription); //tilsvarende her
        descriptionTextView.setText(dishDescription);

        //adding back home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing buttons
        uploadImagebtn = findViewById(R.id.uploadImagebtn);
        showImagesBtn = findViewById(R.id.showImagesBtn);

        // on pressing upload button is called
        uploadImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showUploadImage(dishName);

            }
        });

        // on pressing show images button is called
        showImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showDisplayImagesActivity(dishName);
            }
        });

    }

    private void showUploadImage(String dishName){
        Intent intentUploadImage = new Intent(this, UploadImageActivity.class);
        startActivity(intentUploadImage);

    }
    private void showDisplayImagesActivity(String dishName){
        Intent intentDisplayImg = new Intent(this, DisplayImagesActivity.class);
        startActivity(intentDisplayImg);
    }



    }






