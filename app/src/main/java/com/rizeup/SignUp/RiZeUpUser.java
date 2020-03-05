package com.rizeup.SignUp;

import android.net.Uri;

public class RiZeUpUser {

    private String name;
    private String imageUri;

    public RiZeUpUser() {
    }

    public RiZeUpUser(String name, String imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getImageUri() {
        return imageUri;
    }
}
