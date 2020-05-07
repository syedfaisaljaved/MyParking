package com.faisaljaved.myparking.models;

import android.net.Uri;

public class Images {

    private String mImageName;
    private Uri mImageUri;

    public Images(String mImageName, Uri mImageUri) {
        this.mImageName = mImageName;
        this.mImageUri = mImageUri;
    }

    public Images() {
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName = mImageName;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }
}
