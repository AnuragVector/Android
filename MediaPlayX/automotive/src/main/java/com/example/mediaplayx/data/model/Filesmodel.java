package com.example.mediaplayx.data.model;

import java.util.List;

public class Filesmodel {
    private String filepath;
    private List<Songmodel> songs;

    public List<Songmodel> getSongs() {
        return songs;
    }

    public void setSongs(List<Songmodel> songs) {
        this.songs = songs;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Filesmodel(String name, List<Songmodel> songs){
        this.filepath=name;
        this.songs=songs;
    }
}
