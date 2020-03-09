package com.rizeup.SignUp;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.rizeup.CreateQueue.RizeUpQueue;

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
