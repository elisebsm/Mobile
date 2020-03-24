package com.example.cafeteriaappmuc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cafeteriaappmuc.R;

public class MenuOfTheDayActivity extends AppCompatActivity {

    static String dishName = "DISH_NAME";
    String[] dishArray = {"Pasta Carbonara","Salad","Pastel de Nata","Risotto",
            "Pulled pork","Soup"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_the_day);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.dish_list_element, dishArray);

        ListView listView = (ListView) findViewById(R.id.dishList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                Intent intent = new Intent(view.getContext(), DishActivity.class);
                //finne ut hvilken dish den skal til
                intent.putExtra(dishName, dishArray[position]);
                startActivity(intent);
            }
        });
    }

    public void addNewDish (View view) {
        Intent intent = new Intent(this, AddNewDishActivity.class);
        //legge til ny dish
        startActivity(intent);
    }

}
