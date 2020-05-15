package com.example.cafeteriaappmuc;

import android.annotation.SuppressLint;
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
import java.util.HashMap;
import java.util.List;


@SuppressLint("Registered")
public class OpeningHours extends AppCompatActivity {

    //dublicate in list because CTN bar is open two diff times a day
    List<String> foodServicesTaguspark = Arrays.asList("Tagus Cafeteria", "Red Bar","Green Bar");
    List<String> foodServicesCTN = Arrays.asList("CTN Cafeteria", "CTN Bar","CTN Bar");
    //dublicate in list because complex bar is open two diff times a day
    List<String> foodServicesAlameda = Arrays.asList("Central Bar", "Civil Bar", "Civil Cafeteria","Sena Pastry Shop","Mechy Bar", "AEIST Bar",
            "AEIST Esplanade", "Chemy Bar", "SAS Cafeteria", "Math Cafeteria","Complex Bar","Complex Bar");

    private List<String> openingHours = new ArrayList<>();
    private List<String> closingHours = new ArrayList<>();
    private List<String> foodServicesOpen = new ArrayList<>();
    private  int counter=0;
    private Boolean campusSel = true;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  List<String> CafeteriasOpen(String selectedUserVal, String campus) {

        switch (campus) {
            case "Alameda":

                if (selectedUserVal.equals("Student") || selectedUserVal.equals("General Public")) {
                    openingHours = Arrays.asList("09:00:00", "09:0:00", "12:00:00", "08:00:00", "09:00:00", "09:00:00", "09:00:00", "09:00:00", "09:00:00", "13:30:00", "09:00:00", "14:00:00");
                    closingHours = Arrays.asList("17:00:00", "17:00:00", "15:00:00", "19:00:00", "17:00:00", "17:00:00", "17:00:00", "17:00:00", "21:00:00", "15:00:00", "12:00:00", "17:00:00");

                    //staff, researcher, prof etc
                } else {     //second last element always false, in order not to get dublicate in cafetira
                    openingHours = Arrays.asList("09:00:00", "09:0:00", "12:00:00", "08:00:00", "09:00:00", "09:00:00", "09:00:00", "09:00:00", "09:00:00", "12:00:00", "08:00:00", "09:00:00");
                    closingHours = Arrays.asList("17:00:00", "17:00:00", "15:00:00", "19:00:00", "17:00:00", "17:00:00", "17:00:00", "17:00:00", "21:00:00", "15:00:00", "07:00:00", "17:00:00");
                }
                break;
            case "Taguspark":
                openingHours = Arrays.asList("12:00:00", "08:00:00", "07:00:00");
                closingHours = Arrays.asList("15:00:00", "22:00:00", "19:00:00");
                break;
            case "CTN":
                //capmus equals CTN
                openingHours = Arrays.asList("12:00:00", "08:30:00", "15:30:00");
                closingHours = Arrays.asList("14:00:00", "12:00:00", "16:30:00");
                break;
            default:
                campusSel = false;
                break;
        }
        if (campusSel) {
            for (int i = 0; i < openingHours.size(); i++) {
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

                        if (campus.equals("Alameda")) {
                            foodServicesOpen.add(counter, foodServicesAlameda.get(i));
                            counter++;
                        } else if(campus.equals("Taguspark")){
                            foodServicesOpen.add(counter, foodServicesTaguspark.get(i));
                            counter++;
                        }
                        else{
                            foodServicesOpen.add(counter, foodServicesCTN.get(i));
                            counter++;
                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


        }
        //System.out.println(foodServicesOpen.toString());
        return foodServicesOpen;
    }


}

