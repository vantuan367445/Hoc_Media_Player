package com.example.tuanvatvo.hoc_media_player.model;

import java.io.Serializable;

public class ModelSong implements Serializable {
    private String title;
    private String artist;
    private String loacation;

    public ModelSong(String title, String artist, String loacation) {
        this.title = title;
        this.artist = artist;
        this.loacation = loacation;
    }

    public ModelSong() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLoacation() {
        return loacation;
    }

    public void setLoacation(String loacation) {
        this.loacation = loacation;
    }
}
