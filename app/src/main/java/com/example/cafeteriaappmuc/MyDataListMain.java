package com.example.cafeteriaappmuc;

public class MyDataListMain {

    /**
     * This class is used to get data for list of food services in MainActivity.
     */

    private String textViewFoodService;
    private int textViewEstimatedTimeToWalk;
    private int textViewExpectedQueueWaitingTime;

    public MyDataListMain(String foodService, int timeToWalk, int queueTime) {
        this.textViewFoodService = foodService;
        this.textViewEstimatedTimeToWalk = timeToWalk;
        this.textViewExpectedQueueWaitingTime = queueTime;
    }
    public String getFoodService() {
        return textViewFoodService;
    }
    public void setFoodService(String foodService) {
        this.textViewFoodService = foodService;
    }
    public int getTimeToWalk() {
        return textViewEstimatedTimeToWalk;
    }
    public void setTimeToWalk(int timeToWalk) {
        this.textViewEstimatedTimeToWalk = timeToWalk;
    }
    public int getQueueTime() {
        return textViewExpectedQueueWaitingTime;
    }
    public void setQueueTime(int queueTime) {
        this.textViewExpectedQueueWaitingTime = queueTime;
    }
}