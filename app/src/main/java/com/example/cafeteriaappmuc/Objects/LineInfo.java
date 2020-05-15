package com.example.cafeteriaappmuc.Objects;

public class LineInfo {
    //must have same name as in database
    private int numberInLine;

    public LineInfo(){

    }

    public LineInfo(int numberInLine){
        this.numberInLine=numberInLine;
    }
    public Integer getNumberInLine(){
        return numberInLine;
    }
    public void setNumberInLine(int numberInLine){
        this.numberInLine=numberInLine;

    }
}
