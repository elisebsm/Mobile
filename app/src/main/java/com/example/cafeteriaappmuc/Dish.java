package com.example.cafeteriaappmuc;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Dish {

   public String name;
   public double price;
   public String description;
   public DishType type;

   public Dish() {

   }

    public Dish(String name, double price, String description, DishType type) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
    }

    public String toString() {
        return this.name + " " + this.price + " Euros (" + this.type.toString() +")";
    }

    @Exclude
    public Map<String, Object> toMap() { //lager et Map (dictionary) med alle feltene. Dette fordi det er måten firebase kan lagre data.
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("price", price);
        result.put("description", description);
        result.put("type", type.toString());

        return result;
    }
}
