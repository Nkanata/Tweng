package com.example.tweng;


import com.google.firebase.Timestamp;

public class Music {
    String title;
    String artist;
    String time;
    String album;
    String genre;
    String music_art_url;
    String artist_profile_pic;
    String id;
    String url;
    String username;
    Timestamp timestamp;
    String posted_by;
    String category;



    public Music() {
    }

    public Music(String title, String artist, String timestamp, String album, String genre, String song_art, String artist_profile_pic) {
        this.title = title;
        this.artist = artist;
        this.time = timestamp;
        this.album = album;
        this.genre = genre;
        this.music_art_url = song_art;

        this.artist_profile_pic = artist_profile_pic;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getMusic_art_url() {
        return music_art_url;
    }

    public void setMusic_art_url(String music_art_url) {
        this.music_art_url = music_art_url;
    }

    public String getArtist_profile_pic() {
        return artist_profile_pic;
    }

    public void setArtist_profile_pic(String artist_profile_pic) {
        this.artist_profile_pic = artist_profile_pic;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public void setPosted_by(String posted_by) {
        this.posted_by = posted_by;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
