package com.rizeup.models;

import androidx.annotation.Nullable;

public class RiZeUpUser {

    private String name;
    private String imageUri;
    private String uid;
    private String registeredQ;

    public RiZeUpUser() {
    }



    public RiZeUpUser(String name, String imageUri, String uid, String registeredQ) {
        this.name = name;
        this.imageUri = imageUri;
        this.uid = uid;
        this.registeredQ = registeredQ;
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

    public String getRegisteredQ() {
        return registeredQ;
    }
    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof RiZeUpUser)){
            return false;
        }else {
            RiZeUpUser user = (RiZeUpUser) obj;
            return this.uid.equals(user.uid) && this.imageUri.equals(user.imageUri) &&
                    this.name.equals(user.name);
        }
    }
}
