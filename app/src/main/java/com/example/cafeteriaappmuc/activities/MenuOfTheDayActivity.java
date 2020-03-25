package com.example.cafeteriaappmuc.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cafeteriaappmuc.Dish;
import com.example.cafeteriaappmuc.R;

public class MenuOfTheDayActivity extends AppCompatActivity {

    static String DISH_NAME = "DISH_NAME";
    static String DISH_PRICE = "DISH_PRICE";
    static String DISH_DESCRIPTION = "DISH_DESCRIPTION";

    Dish[] dishes = {new Dish("Pasta Carbonara", 5.4, "Traditional italian dish."),
            new Dish("Salad", 3.5, "Crispy with tomatoes."),
            new Dish("Pastel de Nata", 1.20, "Traditional Portuguese cake from 18th century."),
            new Dish("Risotto", 6.3, "Rice with mushrooms and aspargues."),
            new Dish("Pulled pork", 4.5, "Pork with majonees."),
            new Dish("Soup", 2.40, "Brocolee soup of the house.")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_the_day);

        ArrayAdapter adapter = new ArrayAdapter<Dish>(this,
                R.layout.dish_list_element, dishes);

        ListView listView = (ListView) findViewById(R.id.dishList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish selectedDish = dishes[position];

                Intent intent = new Intent(view.getContext(), DishActivity.class);
                //finne ut hvilken dish den skal til
                intent.putExtra(DISH_NAME, selectedDish.name);
                intent.putExtra(DISH_PRICE, Double.toString(selectedDish.price));
                intent.putExtra(DISH_DESCRIPTION, selectedDish.description);
                startActivity(intent);
            }
        });
    }

    public void addNewDish(View view) {
        Intent intent = new Intent(this, AddNewDishActivity.class);
        //legge til ny dish
        startActivity(intent);
    }
}
