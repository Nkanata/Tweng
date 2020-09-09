package com.example.tweng.chats;

public class Chats {
    String username;
    String preview_text;
    String time;
    String sender_profile_pic;

    public Chats(String username, String text, String time, String sender_profile_pic) {
        this.username = username;
        this.preview_text = text;
        this.time = time;
        this.sender_profile_pic = sender_profile_pic;
    }

    public String getUsername() {
        return username;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPreview_text() {
        return preview_text;
    }

    public void setPreview_text(String preview_text) {
        this.preview_text = preview_text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender_profile_pic() {
        return sender_profile_pic;
    }

    public void setSender_profile_pic(String sender_profile_pic) {
        this.sender_profile_pic = sender_profile_pic;
    }
}
