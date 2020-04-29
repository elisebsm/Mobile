package com.example.cafeteriaappmuc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class OpeningHours extends AppCompatActivity {
    private String openingHours;
    private String closingHours;
    private String studentHoursOpening= "08:00:00";
    private String studentHoursClosing= "10:00:00";
    private String staffHoursOpening= "07:30:00";
    private String staffHoursClosing= "18:00:00";
    private String generalHoursOpening= "09:00:00";
    private String generalHoursClosing = "10:30:00";
    private Boolean isTrue;



    @RequiresApi(api = Build.VERSION_CODES.O)
    public  Boolean isCafeteriaOpen() {
       GlobalClass globalAssetsVariable = (GlobalClass) getApplicationContext();
       String selectedUserProfile=globalAssetsVariable.getProfile();

        if (selectedUserProfile=="Student"){
            openingHours = studentHoursOpening;
            closingHours = studentHoursClosing;


        }
         if (selectedUserProfile=="General Public"){
            openingHours=generalHoursOpening;
            closingHours=generalHoursClosing;
        }
        else {
            openingHours=staffHoursOpening;
            closingHours=staffHoursClosing;

        }

        try {

            Date openTime = new SimpleDateFormat("HH:mm:ss").parse(openingHours);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(openTime);
            calendar1.add(Calendar.DATE, 1);

            Date closingTime = new SimpleDateFormat("HH:mm:ss").parse(closingHours);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(closingTime);
            calendar2.add(Calendar.DATE, 1);

            LocalTime currentTime = LocalTime.now(); // Gets the current time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timeCurrent = currentTime.format(formatter);

            Date d = new SimpleDateFormat("HH:mm:ss").parse(timeCurrent);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);
            Date timeOfNow = calendar3.getTime();

            if (timeOfNow.after(calendar1.getTime()) && timeOfNow.before(calendar2.getTime())) {
                //checkes whether the current time is between openinghours

                isTrue= true;

            }
            else{
                isTrue= false;
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isTrue;
    }


}

