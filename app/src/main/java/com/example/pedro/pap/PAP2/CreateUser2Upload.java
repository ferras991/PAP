package com.example.pedro.pap.PAP2;

public class CreateUser2Upload {

    private String name;
    private String img;

    public CreateUser2Upload() {

    }

    public CreateUser2Upload(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
