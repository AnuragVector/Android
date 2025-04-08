package com.example.mediaplayx.data.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mediaplayx.data.model.Songmodel;

import java.util.List;

@Dao
public interface SongDao {
    @Insert
    void insert(Songmodel song);

    @Update
    void update(Songmodel song);

    @Delete
     void delete(Songmodel song);

    @Query("SELECT * FROM  favoratesongs")
    LiveData<List<Songmodel>> getAllSongs();

    @Query("SELECT * FROM favoratesongs WHERE song_id = :id")
    LiveData<Songmodel> getSongById(int id);

    @Query("SELECT COUNT(*) FROM favoratesongs WHERE song_id = :id")
    int checkSongExists(int id);
}