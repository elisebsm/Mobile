package com.example.cafeteriaappmuc.io;

import com.example.cafeteriaappmuc.Dish;

import java.util.ArrayList;
import java.util.Arrays;

public class LocalDishIO {

    static Dish[] initialDishes = {new Dish("Pasta Carbonara", 5.4, "Traditional italian dish."),
            new Dish("Salad", 3.5, "Crispy with tomatoes."),
            new Dish("Pastel de Nata", 1.20, "Traditional Portuguese cake from 18th century."),
            new Dish("Risotto", 6.3, "Rice with mushrooms and aspargues."),
            new Dish("Pulled pork", 4.5, "Pork with majonees."),
            new Dish("Soup", 2.40, "Brocolee soup of the house.")};

    static ArrayList<Dish> dishes = new ArrayList<>(Arrays.asList(initialDishes));

    public static Dish[] getAllDishes() {
        Dish[] dishArray = new Dish[dishes.size()];
        return dishes.toArray(dishArray);
    }

    public static void saveDish(Dish dish) {
        dishes.add(dish);
    }
}
