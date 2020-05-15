package com.example.cafeteriaappmuc;

public class MyDataListMain {

    /**
     * This class is used to get data for list of food services in MainActivity.
     */

    private String textViewFoodService;
    private String textViewEstimatedTimeToWalk;
    private long textViewExpectedQueueWaitingTime;

    public MyDataListMain(String foodService, String timeToWalk, long queueTime) {
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
    public String getTimeToWalk() {
        return textViewEstimatedTimeToWalk;
    }
    public void setTimeToWalk(String timeToWalk) {
        this.textViewEstimatedTimeToWalk = timeToWalk;
    }
    public long getQueueTime() {
        return textViewExpectedQueueWaitingTime;
    }
    public void setQueueTime(long queueTime) {
        this.textViewExpectedQueueWaitingTime = queueTime;
    }
}
