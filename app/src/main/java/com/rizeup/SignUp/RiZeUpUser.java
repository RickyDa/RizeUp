package com.rizeup.SignUp;

import android.net.Uri;

public class RiZeUpUser {

    private String name;
    private String imageUri;


    private String uid;

    public RiZeUpUser() {
    }

    public RiZeUpUser(String name, String imageUri, String uid) {
        this.name = name;
        this.imageUri = imageUri;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getUid() {
        return uid;
    }
}
