package com.example.cafeteriaappmuc.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.ImageUploadInfo;
import com.example.cafeteriaappmuc.R;

import com.example.cafeteriaappmuc.RecyclerItemClickListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DisplayImageActivity extends AppCompatActivity {

    private String imageName;
    private String databasePath;
    private StorageReference imagesRef;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_image);
        Intent intent = getIntent();
        imageName = intent.getStringExtra("imageName");
        //getting image from firebase or cache
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String foodService= ((GlobalClass) this.getApplication()).getFoodService();
        String dishName= ((GlobalClass) this.getApplication()).getDishName();
        imagesRef = storageReference.child("images/"+foodService+"/"+dishName+"/"+imageName);
        imageView = findViewById(R.id.FullImageView);

        //Loading image from Glide library.
        Glide.with(this).load(imagesRef).into(imageView);


    }

}
