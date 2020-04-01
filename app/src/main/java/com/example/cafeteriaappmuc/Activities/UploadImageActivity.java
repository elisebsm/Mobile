package com.example.cafeteriaappmuc.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cafeteriaappmuc.R;

public class UploadImageActivity extends AppCompatActivity {
    private Button chooseImageButton, uploadImageButton,saveImageButton;
    private EditText editImageTextName;
    private ImageView imagePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        chooseImageButton = (Button) findViewById(R.id.chooseImageButton);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        saveImageButton = (Button) findViewById(R.id.saveImageButton);
        imagePreview = (ImageView) findViewById(R.id.imagePreview);
        editImageTextName = (EditText) findViewById(R.id.editImageTextName);
    }


}
