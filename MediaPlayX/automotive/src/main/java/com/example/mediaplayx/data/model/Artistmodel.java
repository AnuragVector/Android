package com.example.mediaplayx.data.model;

import java.util.List;

public class Artistmodel {

    private String artist_name;
    private List<Songmodel> songs;

    public String getArtist_name() {
        return artist_name;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public List<Songmodel> getSongs() {
        return songs;
    }

    public void setSongs(List<Songmodel> songs) {
        this.songs = songs;
    }
    public Artistmodel(String name, List<Songmodel> songs){
        this.artist_name=name;
        this.songs=songs;
    }

}
