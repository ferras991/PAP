package com.example.pedro.pap.CLASSES.Adapters;

public class CreateProjectClass {

    public String id;
    public String name;
    public String userName;
    public String userId;
    public String url;
    private String imageUrl;

    //private String mKey;

    public String getName(){
        return name;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserId(){
        return userId;
    }

    public String getUrl(){
        return url;
    }

    public String getId(){return id;}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CreateProjectClass(String id, String name, String userName, String userId, String url, String imageUrl) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.userId = userId;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public CreateProjectClass(){

    }
}
