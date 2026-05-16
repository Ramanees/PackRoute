package com.example.swiftdelivery;

public class LiveChat {

    private String sender;
    private String chat;
    private long timestamp;

    public LiveChat() {};

    public String getSender() {
        return sender;
    }

    public String getChat() {
        return chat;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LiveChat(String sender, String chat, long timestamp) {
        this.sender = sender;
        this.chat = chat;
        this.timestamp = timestamp;
    }
}
