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

public class MenuDayActivity extends AppCompatActivity {

    static String DISH_NAME = "DISH_NAME";
    static String DISH_PRICE = "DISH_PRICE";
    static String DISH_DESCRIPTION = "DISH_DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_of_the_day);

        String foodService = ((GlobalClass) this.getApplication()).getFoodService();

        DishIO.getAllDishes(foodService, new DishIO.FirebaseCallback() { //henter alle disher fra DishIO
            @Override
            public void onCallback(List<Dish> list) { //Bruker callback og asynkron kode for å være sikker på å få alle elementene vi trenger før vi kjører koden
                final Dish[] dishes = list.toArray(new Dish[list.size()]);

                ArrayAdapter adapter = new ArrayAdapter<Dish>(getApplicationContext(),
                        R.layout.dish_list_element, dishes);

                ListView listView = (ListView) findViewById(R.id.dishList);
                listView.setAdapter(adapter); //bruker adapter for å fylle en liste
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //lager en listner på "On click" for all dishene, som sender deg til riktig dish
                        Dish selectedDish = dishes[position];

                        Intent intent = new Intent(view.getContext(), DishesActivity.class); //bruker intent og extras til å sende info om dishene til dishActivity
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
        Intent intent = new Intent(this, AddDishActivity.class);
        startActivity(intent);
    }
}