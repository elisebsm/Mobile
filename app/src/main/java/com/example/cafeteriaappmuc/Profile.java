package com.example.cafeteriaappmuc;

import android.app.Application;

public class Profile extends Application {

    private Object userProfile;

    public Object getProfile(){
        return userProfile;
    }
    public void setProfile(Object userProfile){
        this.userProfile =userProfile;
    }

}
