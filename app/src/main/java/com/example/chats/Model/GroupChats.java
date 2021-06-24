package com.example.chats.Model;

public class GroupChats {
    private String message, sender, time, type;


    public GroupChats(String message, String sender, String time, String type) {
        this.message = message;
        this.sender = sender;
        this.time = time;
        this.type = type;
    }

    public GroupChats() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
