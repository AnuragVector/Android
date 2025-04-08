package com.example.mediaplayx.data.model;

import java.util.List;

public class Albummodel {
    private String Album_name;
    private List<Songmodel> songs;

    public String getAlbum_name() {
        return Album_name;
    }

    public void setAlbum_name(String album_name) {
        Album_name = album_name;
    }

    public List<Songmodel> getSongs() {
        return songs;
    }

    public void setSongs(List<Songmodel> songs) {
        this.songs = songs;
    }

    public Albummodel(String album_name, List<Songmodel> song){
        this.Album_name=album_name;
        this.songs=song;

    }
}
