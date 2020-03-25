package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.example.cafeteriaappmuc.R;

public class AddNewDishActivity extends AppCompatActivity {

    static String new_dish_info = "new_Dish_info";
    String[] newDishInfoArray = {"", "", ""};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_dish);

       // ArrayAdapter adapter = new ArrayAdapter<String>(this,
               // R.layout.new_Dish_info, newDishInfoArray);
    }

   // public void onAddDishClick(AdapterView<?> parent, View view, int position, long id) {
       // System.out.println(position);

        //Intent intent = new Intent(view.getContext(), DishActivity.class);
        //finne ut hvilken dish den skal til
      //  intent.putExtra(new_dish_info, newDishInfoArray[position]);
     //   startActivity(intent);¨
    }


    //en funksjon som lagrer den nye dishen og lager en ny greie.
