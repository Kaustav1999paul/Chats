package com.example.chats.Model;

public class Comments {
    private String comment, id, owner, personImage, personName;

    public Comments() {
    }

    public Comments(String comment, String id, String owner, String personImage, String personName) {
        this.comment = comment;
        this.id = id;
        this.owner = owner;
        this.personImage = personImage;
        this.personName = personName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
}
