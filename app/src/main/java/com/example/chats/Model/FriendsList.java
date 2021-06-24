package com.example.chats.Model;

public class FriendsList {
    private String email, id, imageURL, username;

    public FriendsList() {
    }

    public FriendsList(String email, String id, String imageURL, String username) {
        this.email = email;
        this.id = id;
        this.imageURL = imageURL;
        this.username = username;
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
