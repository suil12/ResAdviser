package com.example.prueba2.model;

public class Pet {
    String name, age, color, photo;
    public Pet(){}

    public Pet(String name, String age, String color, String photo) {
        this.name = name;
        this.age = age;
        this.color = color;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
