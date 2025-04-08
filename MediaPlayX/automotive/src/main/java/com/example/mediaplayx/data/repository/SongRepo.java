package com.example.mediaplayx.data.repository;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.example.mediaplayx.data.model.Albummodel;
import com.example.mediaplayx.data.model.Artistmodel;
import com.example.mediaplayx.data.model.Filesmodel;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.source.SongDao;
import com.example.mediaplayx.data.source.SongDatabase;
import com.example.mediaplayx.services.MyMusicService;
import com.example.mediaplayx.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;


public class SongRepo {
    private ContentResolver contentResolver;
    private MediaControllerCompat mediaController;
    private MediaBrowserCompat mediaBrowser;
    private boolean firstsong=false;
    public static final String TAG = SongRepo.class.getSimpleName();
    private SongViewModel songViewModel;

    private List<Artistmodel> artists = new ArrayList<>();
    private List<Songmodel> artistsongs = new ArrayList<>();
    private List<Songmodel> albumsongs= new ArrayList<>();

    private List<Filesmodel> filesmodels=new ArrayList<>();



    public Context context;
    private static SongRepo instance;
    private MutableLiveData<Boolean> isplaying=new MutableLiveData<>();

    private MutableLiveData<Songmodel> metadataLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> seekpositon = new MutableLiveData<>();

    private SongDatabase database;
    private List<Albummodel> albummodels=new ArrayList<>();
    private SongDao songDao;



    private MutableLiveData<List<Songmodel>> likedsongs = new MutableLiveData<>();
    private LiveData<List<Songmodel>> tempsongs;

    public static synchronized SongRepo getInstance(Context context) {
        if (instance == null) {
            instance = new SongRepo(context);
        }
        return instance;
    }


    private SongRepo(Context cont) {
        this.context = cont;
        this.contentResolver = cont.getContentResolver();
        initializeMediaBrowser();
        database = SongDatabase.getDatabase(cont);
        songDao = database.songDao();
        tempsongs = songDao.getAllSongs();
    }

    public LiveData<List<Songmodel>> getlikedsongs() {
        return tempsongs;
    }

    public List<Songmodel> getfavsong(){
        getlikedsongs();
        return likedsongs.getValue();
    }

    public List<Songmodel> getAllSongs() {
        getSongs();

        return allSongs;
    }
    public List<Songmodel> getArtistsongs(){

        return artistsongs;
    }
    public List<Songmodel> getAlbumsongs(){

        return albumsongs;
    }


    public List<Songmodel> getfavallsongs(){

        return tempsongs.getValue();
    }



    public LiveData<Songmodel> getMetadata() {
        return metadataLiveData;
    }

    public LiveData<Integer> getseekposition() {
        return seekpositon;
    }

    public LiveData<Boolean> getisplaying(){
        return isplaying;}


    public void updateMetadata(Songmodel newData) {
        metadataLiveData.setValue(newData);
    }

    private List<Songmodel> allSongs = new ArrayList<>();


    public LiveData<List<Songmodel>> getSongs() {
        MutableLiveData<List<Songmodel>> songsLiveData = new MutableLiveData<>();
        List<Songmodel> songList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION};
        String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1 AND " +
                "(" + MediaStore.Audio.Media.DATA + " LIKE '%.mp3' OR " +
                MediaStore.Audio.Media.DATA + " LIKE '%.m4a')" + " AND " +
                MediaStore.Audio.Media.DURATION + " > 120000";

        Cursor cursor = contentResolver.query(uri, projection, selection, null, null);
        Log.d(TAG, "getSongs: " + cursor.getCount());
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String Artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));


                songList.add(new Songmodel(id, name, Artist, path, album, duration));
            }
            cursor.close();
        }

        songsLiveData.setValue(songList);
        allSongs = songList;


        return songsLiveData;
    }

    private void initializeMediaBrowser() {
        Log.d(TAG, "initializeMediaBrowser: ");
        mediaBrowser = new MediaBrowserCompat(context,
                new ComponentName(context, MyMusicService.class),
                connectionCallback,
                null);
        mediaBrowser.connect();
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            Log.d(TAG, "onConnected: ");
            Checkmediaconnected();
            super.onConnected();
        }

        @Override
        public void onConnectionSuspended() {
            Log.d(TAG, "MediaBrowser connection suspended.");
            mediaController = null;
        }

        @Override
        public void onConnectionFailed() {
            Log.e(TAG, "MediaBrowser connection failed.");
            mediaController = null;
        }
    };
    public void updatplaying(int index){
        if(3==index){
            isplaying.setValue(true);
        } else if (index==2) {
            isplaying.setValue(false);

        }
    }

    private void Checkmediaconnected() {

        if (mediaBrowser != null && mediaBrowser.isConnected()) {
            mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
            Log.d(TAG, "MediaController created successfully.");

            mediaController.registerCallback(new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {

                    int currentPosition = (int) playbackState.getPosition();
                    int isplay=playbackState.getState();
                    updatplaying(isplay);

                    updatePosition(currentPosition);



                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {;
                    Songmodel newData = convertMetadataToMyData(metadata);
                    Log.d(TAG, "onMetadataChanged: form path"+newData.getPath());
                    updateMetadata(newData);

                }
            });
        } else {
            Log.e(TAG, "MediaBrowser is not connected.");
        }

    }

    private Songmodel convertMetadataToMyData(MediaMetadataCompat metadata) {
        int id = Integer.parseInt(metadata.getDescription().getMediaId());
        String name = metadata.getDescription().getTitle().toString();
        String artist = metadata.getDescription().getSubtitle().toString();
        String path = metadata.getDescription().getMediaUri().toString();
        String album = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM);
        long duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);

        return new Songmodel(id, name, artist, path, album, duration);
    }

    public void playselectedSong(int songid, String category) {

        if (mediaController != null) {

            Bundle extras = new Bundle();
            extras.putString("category", category);

            mediaController.getTransportControls().playFromMediaId(String.valueOf(songid), extras);
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }
    }

    public void playsong() {
        if (mediaController != null) {
            mediaController.getTransportControls().play();
            Log.d(TAG, "playsong: onplay from repo");
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }

    }

    public void pauseSong() {
        if (mediaController != null) {
            mediaController.getTransportControls().pause();
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }
    }

    public void playforward() {

        if (mediaController != null) {
            mediaController.getTransportControls().skipToNext();
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }

    }

    public void playbackward() {
        if (mediaController != null) {
            mediaController.getTransportControls().skipToPrevious();
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }
    }

    public void seekTo(int progress) {

        if (mediaController != null) {
            mediaController.getTransportControls().seekTo(progress);
        } else {
            Log.e(TAG, "MediaController is not initialized.");
        }

    }

    public void updatePosition(int position) {

        seekpositon.setValue(position);


    }



    public void insertFavoriteSong(Songmodel song) {
        Log.d(TAG, "insertFavoriteSong: ");
        song.setFavorate(true);
        new Thread(() -> {
            if (songDao.checkSongExists(song.getSong_id()) == 0) {

                songDao.insert(song);
            }
        }).start();
    }

    public void delete(Songmodel song) {

        new Thread(() -> songDao.delete(song)).start();
    }

    public List<Artistmodel> getArtist() {
        List<Songmodel> songs = getAllSongs();

        for (Songmodel s : songs) {
            boolean artistExists = false;

            // Check if the artist already exists
            for (Artistmodel artist : artists) {
                if (artist.getArtist_name().equals(s.getArtist())) {
                    Integer flag = 0;


                    List<Songmodel> ss;
                    ss = artist.getSongs();
                    for (Songmodel songmodel : ss) {
                        if (s.getSong_id() == songmodel.getSong_id()) {
                            flag = 1;
                            break;
                        } else flag = 0;
                    }
                    if (flag == 0) {
                        artist.getSongs().add(s);
                    }
                    artistExists = true;
                    break;
                }
            }

            // If artist doesn't exist, create a new entry
            if (!artistExists) {
                List<Songmodel> newSongList = new ArrayList<>();
                newSongList.add(s);
                artists.add(new Artistmodel(s.getArtist(), newSongList));

            }
        }
        return artists;
    }
    public List<Albummodel> getAlbum(){
        List<Songmodel> songs = getAllSongs();

        for (Songmodel s : songs) {
            boolean AlbumExists = false;

            // Check if the artist already exists
            for (Albummodel album : albummodels) {
                if (album.getAlbum_name().equals(s.getAlbums())) {
                    Integer flag = 0;

                    List<Songmodel> ss;
                    ss = album.getSongs();
                    for (Songmodel songmodel : ss) {
                        if (s.getSong_id() == songmodel.getSong_id()) {
                            flag = 1;
                            break;
                        } else flag = 0;
                    };
                    if (flag == 0) {
                        album.getSongs().add(s);
                    }
                    AlbumExists = true;
                    break;
                }
            }

            // If artist doesn't exist, create a new entry
            if (!AlbumExists) {
                List<Songmodel> newSongList = new ArrayList<>();
                newSongList.add(s);
                albummodels.add(new Albummodel(s.getAlbums(), newSongList));

            }
        }
        Log.d(TAG, "getAlbum: "+albummodels);
        return albummodels;

    }
    public List<Songmodel>getselectedartist(String name){

        for(Artistmodel artist :  getArtist()){
            if(artist.getArtist_name().equals(name)){
                artistsongs=artist.getSongs();

                return artist.getSongs();

            }
        }
        return  new ArrayList<>();
    }

    public List<Songmodel>getselectedalbum(String name){

        for(Albummodel album : getAlbum()){
            if(album.getAlbum_name().equals(name)){
                albumsongs=album.getSongs();
                return album.getSongs();

            }
        }
        return new ArrayList<>();
    }

    public List<Songmodel> filterSongs(String query) {
        List<Songmodel> filteredSongs = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            return allSongs; // Return all songs if the query is empty
        }

        String lowerCaseQuery = query.toLowerCase(); // Convert query to lower case
        for (Songmodel song : getAllSongs()) {
            // Check if the song name or artist matches the query
            if (song.getSong_name().toLowerCase().contains(lowerCaseQuery) ||
                    song.getArtist().toLowerCase().contains(lowerCaseQuery)) {
                filteredSongs.add(song); // Add matching song to the filtered list
            }
        }
        return filteredSongs; // Return the filtered list
    }

    public void getfff(){
        List<Songmodel> songs = getAllSongs();
       String[] FILENAME={};
        for (Songmodel s : songs) {
            boolean FilesExists = false;
                String path = s.getPath();
                String[] parts = path.split("/");
                for (String part : parts) {
                    Log.d(TAG, "getfff: "+part);                }


        }



    }

    public List<Filesmodel> getFiles(){

        List<Songmodel> songs = getAllSongs();

        for (Songmodel s : songs) {
            boolean FilesExists = false;


            // Check if the artist already exists
            for (Filesmodel  file: filesmodels) {
                if (file.getFilepath().equals(s.getPath())) {
                    Integer flag = 0;

                    List<Songmodel> ss;
                    ss = file.getSongs();
                    for (Songmodel songmodel : ss) {
                        if (s.getSong_id() == songmodel.getSong_id()) {
                            flag = 1;
                            break;
                        } else flag = 0;
                    };
                    if (flag == 0) {
                        file.getSongs().add(s);
                    }
                    FilesExists = true;
                    break;
                }
            }

            // If artist doesn't exist, create a new entry
            if (!FilesExists) {
                List<Songmodel> newSongList = new ArrayList<>();
                newSongList.add(s);
                filesmodels.add(new Filesmodel(s.getPath(), newSongList));

            }
        }

        return filesmodels;

    }

    }