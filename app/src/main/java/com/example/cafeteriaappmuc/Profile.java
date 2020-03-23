package com.example.cafeteriaappmuc;

import android.app.Application;

public class Profile extends Application {

    private String userProfile;

    public String getProfile(){
        return userProfile;
    }
    public void setProfile(String userProfile){
        this.userProfile =userProfile;
    }

}
