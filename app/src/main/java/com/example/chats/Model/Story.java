package com.example.chats.Model;

public class Story {
    private String content, id, owner, personImage, personName, time, today, tomorrow;

    public Story() {
    }

    public Story(String content, String id, String owner, String personImage, String personName, String time, String today, String tomorrow) {
        this.content = content;
        this.id = id;
        this.owner = owner;
        this.personImage = personImage;
        this.personName = personName;
        this.time = time;
        this.today = today;
        this.tomorrow = tomorrow;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(String tomorrow) {
        this.tomorrow = tomorrow;
    }
}
