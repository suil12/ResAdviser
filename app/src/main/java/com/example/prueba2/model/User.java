package com.example.prueba2.model;
import com.google.firebase.firestore.Exclude;

public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String postalCode;

    // Constructor vacío requerido para Firestore
    public User() {}

    public User(String name, String email, String password, String phone, String postalCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.postalCode = postalCode;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude // Excluye el campo de la serialización a Firestore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
