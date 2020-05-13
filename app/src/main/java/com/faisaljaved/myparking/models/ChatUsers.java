package com.faisaljaved.myparking.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatUsers implements Parcelable {

    private String adImage;
    private String sellerImage;
    private String buyerImage;
    private String sellerUsername;
    private String buyerUsername;
    private String adTitle;
    private String adId;
    private String sellerUid;
    private String buyerUid;

    public ChatUsers() {
    }

    public ChatUsers(String adImage, String sellerImage,String buyerImage, String sellerUsername, String buyerUsername, String adTitle, String adId, String sellerUid, String buyerUid) {
        this.adImage = adImage;
        this.sellerImage = sellerImage;
        this.buyerImage = buyerImage;
        this.sellerUsername = sellerUsername;
        this.buyerUsername = buyerUsername;
        this.adTitle = adTitle;
        this.adId = adId;
        this.sellerUid = sellerUid;
        this.buyerUid = buyerUid;
    }

    protected ChatUsers(Parcel in) {
        adImage = in.readString();
        sellerImage = in.readString();
        buyerImage = in.readString();
        sellerUsername = in.readString();
        buyerUsername = in.readString();
        adTitle = in.readString();
        adId = in.readString();
        sellerUid = in.readString();
        buyerUid = in.readString();
    }

    public static final Creator<ChatUsers> CREATOR = new Creator<ChatUsers>() {
        @Override
        public ChatUsers createFromParcel(Parcel in) {
            return new ChatUsers(in);
        }

        @Override
        public ChatUsers[] newArray(int size) {
            return new ChatUsers[size];
        }
    };

    public String getAdImage() {
        return adImage;
    }

    public String getSellerImage() {
        return sellerImage;
    }

    public String getBuyerImage() {
        return buyerImage;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public String getAdId() {
        return adId;
    }

    public String getSellerUid() {
        return sellerUid;
    }

    public String getBuyerUid() {
        return buyerUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(adImage);
        parcel.writeString(sellerImage);
        parcel.writeString(buyerImage);
        parcel.writeString(sellerUsername);
        parcel.writeString(buyerUsername);
        parcel.writeString(adTitle);
        parcel.writeString(adId);
        parcel.writeString(sellerUid);
        parcel.writeString(buyerUid);
    }
}
