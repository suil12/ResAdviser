package com.example.prueba2.model;
public class Review {
    private String title;
    private String description;
    private float rating;
    private String restaurantname, username;

    // Costruttore
    Review(){

    }




    public Review(String title, String description, float rating, String restaurantname, String username) {
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.restaurantname= restaurantname;
        this.username= username;
    }

    // Metodi getter e setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public String getRestaurantname() {
        return restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        this.restaurantname = restaurantname;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}