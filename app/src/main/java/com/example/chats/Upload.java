package com.example.chats;

import android.net.Uri;

public class Upload {
    private String mName, mImageUlr;

    public Upload(String trim, Uri uploadSessionUri){

    }

    public Upload(String mName, String mImageUlr) {
        this.mName = mName;
        this.mImageUlr = mImageUlr;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUlr() {
        return mImageUlr;
    }

    public void setmImageUlr(String mImageUlr) {
        this.mImageUlr = mImageUlr;
    }
}
