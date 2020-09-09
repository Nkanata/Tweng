package com.example.tweng.explore;

public class Trends {
    String song_title;
    String artist;
    String time;
    String album;
    String art;


    public Trends(String song_title, String artist, String time, String album) {
        this.song_title = song_title;
        this.artist = artist;
        this.time = time;
        this.album = album;
        this.art = art;

    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
