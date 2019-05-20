package com.example.pedro.pap.PAP;

public class APKUpload {

    public String name;
    public String url;

    public String getName(){
        return name;
    }

    public String getUrl(){
        return url;
    }

    public APKUpload(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public APKUpload(){

    }
}
