package com.faisaljaved.myparking.models;

public class UserDetails {

    private String username;
    private String number;
    private String email;
    private String password;
    private String image;

    public UserDetails(String username, String number, String email, String password, String image) {
        this.username = username;
        this.number = number;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public UserDetails() {
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
