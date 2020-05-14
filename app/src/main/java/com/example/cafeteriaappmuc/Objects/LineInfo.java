package com.example.cafeteriaappmuc.Objects;

public class LineInfo {
    //must have same name as in database
    private Integer numberInLine;

    public LineInfo(){

    }

    public LineInfo(Integer numberInLine){
        this.numberInLine=numberInLine;
    }
    public Integer getNumberInLine(){
        return numberInLine;
    }
    public void setNumberInLine(Integer numberInLine){
        this.numberInLine=numberInLine;

    }
}
