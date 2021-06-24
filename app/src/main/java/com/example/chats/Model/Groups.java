package com.example.chats.Model;

public class Groups {
    private String id, date, name, photo, time;

    public Groups() {
    }

    public Groups(String date, String name, String photo, String time) {
        this.date = date;
        this.name = name;
        this.photo = photo;
        this.time = time;
    }

    public Groups(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
