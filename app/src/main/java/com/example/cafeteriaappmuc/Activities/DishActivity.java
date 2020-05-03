package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;


import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.ImageUploadInfo;
import com.example.cafeteriaappmuc.R;
import com.example.cafeteriaappmuc.RecyclerItemClickListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cafeteriaappmuc.Adapter.RecyclerViewAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DishActivity extends AppCompatActivity  {

    //for displayImages

    private Button uploadImagebtn;

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();

    private String foodService;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        final Intent intent = getIntent();
        final String dishName = intent.getStringExtra(FoodServiceActivity.DISH_NAME); //get exas that are sent with intent, get dish name
        String dishPrice = intent.getStringExtra(FoodServiceActivity.DISH_PRICE);
        String dishDescription = intent.getStringExtra(FoodServiceActivity.DISH_DESCRIPTION);
        foodService=((GlobalClass) this.getApplication()).getFoodService();
        TextView nameTextView = findViewById(R.id.dishName);
        nameTextView.setText(dishName);

        TextView priceTextView = findViewById(R.id.dishPrice); //id in xml where price actually is
        priceTextView.setText(dishPrice + " Euros");

        ((GlobalClass) this.getApplication()).setDishName(dishName);

        TextView descriptionTextView = findViewById(R.id.dishDescription); //same here
        descriptionTextView.setText(dishDescription);

        //adding back home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing buttons
        uploadImagebtn = findViewById(R.id.uploadImagebtn);
      //  showImagesBtn = findViewById(R.id.showImagesBtn);

        // on pressing upload button is called
        uploadImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showUploadImage(dishName);

            }
        });



        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerViewImage);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(DishActivity.this));

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DishActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();

        // Setting up Firebase image upload folder path in databaseReference.

        databaseReference = FirebaseDatabase.getInstance().getReference("Dishes/"+foodService+"/"+dishName + "/images");
        final Intent intentDisplayImage = new Intent(this, DisplayImageActivity.class);

        //viewing images in full size when click
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        ImageUploadInfo imageUpload =list.get(position);
                        String imageName= imageUpload.getImageName();
                        intentDisplayImage.putExtra("imageName", imageName);

                        startActivity(intentDisplayImage);


                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                    list.add(imageUploadInfo);
                }

                adapter = new RecyclerViewAdapter(getApplicationContext(), list,foodService,dishName);

                recyclerView.setAdapter(adapter);



                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });


    }

    private void showUploadImage(String dishName){
        Intent intentUploadImage = new Intent(this, UploadImageActivity.class);
        startActivity(intentUploadImage);

    }


    }






