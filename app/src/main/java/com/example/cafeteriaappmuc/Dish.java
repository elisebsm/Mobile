package com.example.cafeteriaappmuc;

public class Dish {

   public String name;
   public double price;
   public String description;

    public Dish(String name, double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String toString() {
        return this.name + " " + this.price + " Euros";
    }
}
