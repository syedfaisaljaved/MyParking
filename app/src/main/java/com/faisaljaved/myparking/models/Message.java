package com.faisaljaved.myparking.models;

public class Message {

    private String message;
    private String timestamp;
    private String type;
    private String senderUid;

    public Message() {
    }

    public Message(String message, String timestamp, String type, String senderUid) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getSenderUid() {
        return senderUid;
    }
}
