package com.example.tweng.comments;

import com.google.firebase.Timestamp;

public class Comment {
    String comment;
    Timestamp timestamp;
    String username;
    String profile_pic;
    String post_id;
    String posted_by;

    public Comment(String comment, Timestamp timestamp, String username, String profile_pic) {
        this.comment = comment;
        this.timestamp = timestamp;
        this.username = username;
        this.profile_pic = profile_pic;
    }

    public Comment() {
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
