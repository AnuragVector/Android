package com.example.mediaplayx.data.source;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.example.mediaplayx.data.model.Songmodel;

@Database(entities = {Songmodel.class}, version = 1, exportSchema = false)
public abstract class SongDatabase extends RoomDatabase {

    public abstract SongDao songDao();

    private static volatile SongDatabase INSTANCE;

    public static SongDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SongDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SongDatabase.class, "song_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
