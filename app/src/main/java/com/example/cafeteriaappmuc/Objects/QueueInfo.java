package com.example.cafeteriaappmuc.Objects;

import java.util.List;

public class QueueInfo {


    //getting info from database Yi and Xi, must have same name as in database
    private int xi;
    private double yi;

    public QueueInfo(){

    }


    public QueueInfo(int xi, double yi){
        this.xi= xi;
        this.yi= yi;

    }


    public Integer getXi() {
        return xi;
    }
    public void setXi(int xi){
        this.xi=xi;
    }
    public void setYi(double yi){
        this.yi=yi;
    }

    public Double getYi() {
        return yi;
    }



}
