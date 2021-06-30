package com.example.chats.Model;

public class Clips {
    private String author, id, message, title, time, videourl;

    public Clips() {
    }

    public Clips(String author, String id, String message, String title, String time, String videourl) {
        this.author = author;
        this.id = id;
        this.message = message;
        this.title = title;
        this.time = time;
        this.videourl = videourl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }
}
