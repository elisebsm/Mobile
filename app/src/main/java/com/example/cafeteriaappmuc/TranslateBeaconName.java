package com.example.cafeteriaappmuc;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class TranslateBeaconName {

    public static String transToCafeteria(String campus, String beaconName){
        Hashtable<String, String> beaconNameTranslator = new Hashtable<String, String>();

        List<String> foodServicesAlameda = Arrays.asList("Central Bar", "Civil Bar", "Civil Cafeteria","Sena Pastry Shop","Mechy Bar", "AEIST Bar",
                "AEIST Esplanade", "Chemy Bar", "SAS Cafeteria", "Math Cafeteria","Complex Bar");

        List<String> foodServiceAlBeacon = Arrays.asList("CentralBar", "CivilBar", "CivilCafeteria","Sena","MechyBar", "AEISTBar",
                "AEISTEsplanade", "ChemyBar", "SASCafeteria", "MathCafeteria","ComplexBar");

        String transCafeteriaName;

        if (campus.equals("Alameda")){
            for(int i=0; i<foodServicesAlameda.size(); i++){
                beaconNameTranslator.put(foodServiceAlBeacon.get(i),foodServicesAlameda.get(i));
            }
        }
        else if (campus.equals("Taguspark")){
            beaconNameTranslator.put( "TagusCafeteria","Tagus Cafeteria");
            beaconNameTranslator.put("RedBar","Red Bar");
            beaconNameTranslator.put("GreenBar", "Green Bar");

        }
        else{
            beaconNameTranslator.put( "CTNCafeteria","CTN Cafeteria");
            beaconNameTranslator.put("CTNBar","CTN Bar");
        }
        transCafeteriaName = beaconNameTranslator.get(beaconName);

        return transCafeteriaName;
    }


    public static String transToBeacon(String campus, String foodserviceName){

        Hashtable<String, String> beaconNameTranslator = new Hashtable<String, String>();

        List<String> foodServicesAlameda = Arrays.asList("Central Bar", "Civil Bar", "Civil Cafeteria","Sena Pastry Shop","Mechy Bar", "AEIST Bar",
                "AEIST Esplanade", "Chemy Bar", "SAS Cafeteria", "Math Cafeteria","Complex Bar");

        List<String> foodServiceAlBeacon = Arrays.asList("CentralBar", "CivilBar", "CivilCafeteria","Sena","MechyBar", "AEISTBar",
                "AEISTEsplanade", "ChemyBar", "SASCafeteria", "MathCafeteria","ComplexBar");

        if (campus.equals("Alameda")){
            for(int i=0; i<foodServicesAlameda.size(); i++){
                beaconNameTranslator.put(foodServicesAlameda.get(i),foodServiceAlBeacon.get(i));
            }
        }
        else if (campus.equals("Taguspark")){
            beaconNameTranslator.put( "Tagus Cafeteria","TagusCafeteria");
            beaconNameTranslator.put("Red Bar","RedBar");
            beaconNameTranslator.put("Green Bar","GreenBar");

        }
        else{
            beaconNameTranslator.put( "CTNCafeteria","CTN Cafeteria");
            beaconNameTranslator.put("CTNBar","CTN Bar");
        }
        String transToBeaconName= beaconNameTranslator.get(foodserviceName);
        return transToBeaconName;

    }
}
