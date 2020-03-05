package com.rizeup.utils;

import java.io.Serializable;
import java.net.URI;

public class User implements Serializable {

    private String name;
    private String email;
    private String imageUrl;

    public User() {
    }

    public User(String name, String email, String image_uri) {
        this.name = name;
        this.email = email;
        this.imageUrl = image_uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
