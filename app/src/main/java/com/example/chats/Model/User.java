package com.example.chats.Model;

public class User {
    private String bio, email, id, imageURL, username, status, phno;

    public User(){

    }

    public User(String status) {
        this.status = status;
    }

    public User(String bio, String email, String id, String imageURL, String username, String phno) {
        this.bio = bio;
        this.email = email;
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
        this.phno = phno;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
