package com.faisaljaved.myparking.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MyAdData implements Parcelable {

    private String mUID;
    private String mAdId;
    private String mTitle;
    private String mDescription;
    private String mPrice;
    private String mVehicleType;
    private String mLocation;
    private Long mTimestamp;
    private List<String> mImages;

    public MyAdData(String adId, String uid, String mTitle, String mDescription, String mPrice, String mVehicleType, String location, Long timestamp, List<String> mImages) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mPrice = mPrice;
        this.mVehicleType = mVehicleType;
        this.mLocation = location;
        this.mTimestamp = timestamp;
        this.mImages = mImages;
        this.mUID = uid;
        this.mAdId = adId;
    }

    public MyAdData() {
    }


    protected MyAdData(Parcel in) {
        mUID = in.readString();
        mAdId = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mPrice = in.readString();
        mVehicleType = in.readString();
        mLocation = in.readString();
        if (in.readByte() == 0) {
            mTimestamp = null;
        } else {
            mTimestamp = in.readLong();
        }
        mImages = in.createStringArrayList();
    }

    public static final Creator<MyAdData> CREATOR = new Creator<MyAdData>() {
        @Override
        public MyAdData createFromParcel(Parcel in) {
            return new MyAdData(in);
        }

        @Override
        public MyAdData[] newArray(int size) {
            return new MyAdData[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getVehicleType() {
        return mVehicleType;
    }

    public void setVehicleType(String mVehicleType) {
        this.mVehicleType = mVehicleType;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public Long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public List<String> getImages() {
        return mImages;
    }

    public void setImages(List<String> mImages) {
        this.mImages = mImages;
    }

    public String getUID() {
        return mUID;
    }

    public void setUID(String mUID) {
        this.mUID = mUID;
    }

    public String getAdId() {
        return mAdId;
    }

    public void setAdId(String mAdId) {
        this.mAdId = mAdId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUID);
        parcel.writeString(mAdId);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeString(mPrice);
        parcel.writeString(mVehicleType);
        parcel.writeString(mLocation);
        if (mTimestamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(mTimestamp);
        }
        parcel.writeStringList(mImages);
    }
}
