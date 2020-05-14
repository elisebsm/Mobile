package com.example.cafeteriaappmuc;



import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.List;


//least square algorithm
public class QueueAlgorithm extends AppCompatActivity {

    private static Double b1;  //estimate of slope coefficient betta1. Estimate of people in line
    private static Double b0;      //this is time in front of line, estimate
    private static Double meanX;           // average number of people in line
    private static Double meanY;   //average of total waiting time,
    private static Integer n; /// number of people in line
   // private static Double estimateY;


    //This is the real data (X and Y) of people in line
    //private static List<Double> Yi = Arrays.asList(0.5,1.7,3.9); // real data of time per pers in the line
     //private static List<Integer> Xi = Arrays.asList(1,3,5); //number of people in line measured when you arrived


    //training alborithm (should take in Xi and Yi)
    //estimate b1 and b0 aka a and b
    public static Double getB1(List<Integer> Xi, List<Double> Yi ){
        Double sumX=0.0;
        Double sumY=0.0;
        Double Sxy=0.0;
        Double Sxx=0.0;
        n= Xi.size();

        for(int i=0; i <Xi.size() ; i++){
            sumY= sumY+Yi.get(i);
            sumX= sumX+Xi.get(i);

        }

        meanX=sumX/n;
        meanY=sumY/n;

        //calculating b1
        for(int i=0; i <Xi.size() ; i++){
            Sxy= Sxy+(Xi.get(i)-meanX)*(Yi.get(i)-meanY);
            Sxx=Sxx+(Math.pow((Xi.get(i)-meanX),2.0));
        }
        //b1 is amount of time per user in line
        b1=Sxy/Sxx;

        return b1;  //estimate of waiting time based on prev data
    }
    public static Double getb0(List<Integer> Xi, List<Double> Yi){
        n= Xi.size();
        Double sumX=0.0;
        Double sumY=0.0;
        for(int i=0; i <Xi.size() ; i++){
            sumY= sumY+Yi.get(i);
            sumX= sumX+Xi.get(i);

        }
        meanX=sumX/n;
        meanY=sumY/n;


        //calc b0, amount of time used in front of line (in the casheer)
        b0=(sumY-b1*sumX)/n;
        return b0;
    }

    //returnerer b1 og b0, dette er av interesse.
    //starte med 2 kunder
    //liste med feks 50 verdier av Xi og Yi, fjerne elste data.
    //b1 og b0 kalkulere hver gang.





}

