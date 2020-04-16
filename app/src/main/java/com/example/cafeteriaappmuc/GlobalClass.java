package com.example.cafeteriaappmuc;

import android.app.Application;

public class GlobalClass extends Application {

    private String userProfile;
    private String dishName;
    private String foodService;

    public String getProfile(){
        return userProfile;
    }
    public void setProfile(String userProfile){
        this.userProfile =userProfile;
    }

    public void setFoodService(String foodService){
        this.foodService = foodService;
        this.dishName = dishName;
    }

    public String getFoodService(){
        return foodService;
    }

    public void setDishName(String dishName){
        this.dishName=dishName;
    }
    public String getDishName(){
        return dishName;
    }

    //TODO: legge til åpningstider for hver gruppe/bruker. Finen metode å bruke dette
}
