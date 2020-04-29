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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class OpeningHours extends AppCompatActivity {


    private List<String> foodServices = Arrays.asList("Main Building", "Civil Building", "North Tower", "Mechanics Building II", "AEIST Building", " Copy Section");
    private List<String> openingHours = new ArrayList<>();
    private List<String> closingHours = new ArrayList<>();
    private List<String> foodServicesOpen = new ArrayList<>();
    //List<String> foodServicesClosed = new ArrayList<>();
    private  int counter=0;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  List<String> CafeteriasOpen(String selectedUserVal) {

        //GlobalClass globalAssetsVariable = (GlobalClass) getApplicationContext();

        if (selectedUserVal.equals("Student")){
            openingHours = Arrays.asList("09:00:00","07:30:00","08:00:00","08:00:00","08:30:00","08:00:00");
            closingHours= Arrays.asList("17:00:00","19:00:00","16:00:00","16:00:00","16:30:00","18:00:00");

        }
         else if(selectedUserVal.equals("General Public")){
             openingHours = Arrays.asList("09:00:00","09:30:00","10:00:00","08:00:00","08:30:00","09:00:00");
             closingHours= Arrays.asList("17:00:00","18:00:00","14:00:00","16:00:00","15:30:00","15:00:00");
        }
         //staff, researcher, prof etc
        else {
            openingHours =Arrays.asList("07:00:00","07:30:00","08:00:00","07:00:00","07:30:00","08:00:00");
            closingHours= Arrays.asList("17:00:00","15:00:00","17:00:00","14:00:00","14:30:00","16:00:00");

        }

        for (int i=0 ; i <openingHours.size(); i++){
            try {

                Date openTime = new SimpleDateFormat("HH:mm:ss").parse(openingHours.get(i));
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(openTime);
                calendar1.add(Calendar.DATE, 1);

                Date closingTime = new SimpleDateFormat("HH:mm:ss").parse(closingHours.get(i));
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
                    //checkes whether the current time is between


                    foodServicesOpen.add(counter,foodServices.get(i));
                    counter++;
                }




            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println(counter);
        return foodServicesOpen;


    }


}

