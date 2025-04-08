package com.example.mediaplayx.data.model;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favoratesongs")


public class Songmodel {


    @PrimaryKey(autoGenerate = true)
    private int song_id;
    private String song_name;
    private String artist;

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    private String Albums;

    private long duration;

    private boolean favorate=false;

    public Songmodel() {
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getAlbums() {
        return Albums;
    }

    public void setAlbums(String albums) {
        Albums = albums;
    }

    public boolean isFavorate() {
        return favorate;
    }

    public void setFavorate(boolean favorate) {
        this.favorate = favorate;
    }

    public Songmodel(int id, String name, String artist, String path, String albums, long duration){
        this.song_id=id;
        this.song_name=name;
        this.artist=artist;
        this.path=path;
        this.duration=duration;
        this.favorate=false;
        this.Albums=albums;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return this.path;
    }
}
