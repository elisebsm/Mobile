package com.example.cafeteriaappmuc;

public class Dish {

   public String name;
   public double price;
   public String description;
   public DishType type;

    public Dish(String name, double price, String description, DishType type) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
    }

    public String toString() {
        return this.name + " " + this.price + " Euros (" + this.type.toString() +")";
    }
}
