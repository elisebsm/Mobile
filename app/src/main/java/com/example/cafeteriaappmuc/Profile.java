package com.example.cafeteriaappmuc;

import android.app.Application;

public class Profile extends Application {

    private String userProfile;
    private Boolean studentHours;

    public String getProfile(){
        return userProfile;
    }
    public void setProfile(String userProfile){
        this.userProfile =userProfile;
    }

    //TODO: legge til åpningstider for hver gruppe/bruker. Finen metode å bruke dette
}
