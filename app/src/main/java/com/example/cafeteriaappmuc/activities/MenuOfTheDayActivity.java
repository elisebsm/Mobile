package com.example.cafeteriaappmuc.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cafeteriaappmuc.Dish;
import com.example.cafeteriaappmuc.GlobalClass;
import com.example.cafeteriaappmuc.R;
import com.example.cafeteriaappmuc.io.DishIO;

import java.util.List;

public class MenuOfTheDayActivity extends AppCompatActivity {

    static String DISH_NAME = "DISH_NAME";
    static String DISH_PRICE = "DISH_PRICE";
    static String DISH_DESCRIPTION = "DISH_DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_the_day);

        String foodService = ((GlobalClass) this.getApplication()).getFoodService();

        DishIO.getAllDishes(foodService, new DishIO.FirebaseCallback() {
            @Override
            public void onCallback(List<Dish> list) {
                final Dish[] dishes = list.toArray(new Dish[list.size()]);

                ArrayAdapter adapter = new ArrayAdapter<Dish>(getApplicationContext(),
                        R.layout.dish_list_element, dishes);

                ListView listView = (ListView) findViewById(R.id.dishList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Dish selectedDish = dishes[position];

                        Intent intent = new Intent(view.getContext(), DishActivity.class);
                        intent.putExtra(DISH_NAME, selectedDish.name);
                        intent.putExtra(DISH_PRICE, Double.toString(selectedDish.price));
                        intent.putExtra(DISH_DESCRIPTION, selectedDish.description);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    //Add new dish
    public void goToAddNewDish(View view) {
        Intent intent = new Intent(this, AddNewDishActivity.class);
        startActivity(intent);
    }
}