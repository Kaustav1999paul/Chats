package com.example.chats.Model;

public class Chat {
    private String sender, receiver, message, time, type, id, isLiked;
    private Boolean isseen;
    private Boolean  isTextOnly;
    public Chat(){}

    public Chat(String id,String sender, String receiver, String message,Boolean isseen, Boolean isTextOnly, String type,String isLiked) {
        this.sender = sender;
        this.receiver = receiver;
        this.id = id;
        this.message = message;
        this.isseen = isseen;
        this.type = type;
        this.isTextOnly = isTextOnly;
        this.isLiked = isLiked;
    }

    public String getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(String isLiked) {
        this.isLiked = isLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getTextOnly() {
        return isTextOnly;
    }

    public void setTextOnly(Boolean textOnly) {
        isTextOnly = textOnly;
    }

    public Chat(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



    public Boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
