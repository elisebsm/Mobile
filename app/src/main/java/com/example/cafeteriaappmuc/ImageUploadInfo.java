package com.example.cafeteriaappmuc;

public class ImageUploadInfo {

    public String imageName;

    public String imageURL;

    public String dishName;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url, String dishName) {

        this.imageName = name;
        this.imageURL= url;
        this.dishName = dishName;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageDishName() {return dishName;}

}