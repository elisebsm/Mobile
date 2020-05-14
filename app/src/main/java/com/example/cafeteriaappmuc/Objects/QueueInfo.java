package com.example.cafeteriaappmuc.Objects;

import java.util.List;

public class QueueInfo {


    //getting info from database Yi and Xi, must have same name as in database
    private Integer Xi;
    private Long Yi;

    public QueueInfo(){

    }


    public void QueueInfoY(Integer Xi, Long Yi){
        this.Xi= Xi;
        this.Yi= Yi;

    }


    public Integer getXi() {
        return Xi;
    }
    public void setXi(){
        this.Xi=Xi;
    }
    public void setYi(){
        this.Yi=Yi;
    }

    public Long getYi() {
        return Yi;
    }



}
