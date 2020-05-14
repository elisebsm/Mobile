package com.example.cafeteriaappmuc.Objects;

import java.util.List;

public class QueueInfo {


    //getting info from database Yi and Xi, must have same name as in database
    private Integer xi;
    private Double yi;

    public QueueInfo(){

    }


    public QueueInfo(Integer xi, Double yi){
        this.xi= xi;
        this.yi= yi;

    }


    public Integer getXi() {
        return xi;
    }
    public void setXi(Integer xi){
        this.xi=xi;
    }
    public void setYi(Double yi){
        this.yi=yi;
    }

    public Double getYi() {
        return yi;
    }



}
