package com.example.mediaplayx.ui.viewmodel;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mediaplayx.data.model.Albummodel;
import com.example.mediaplayx.data.model.Artistmodel;
import com.example.mediaplayx.data.model.Filesmodel;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.repository.SongRepo;

import java.util.ArrayList;
import java.util.List;

public class SongViewModel extends ViewModel {

    private MutableLiveData<Integer> category=new MutableLiveData<>();
    private MutableLiveData<String> artistname=new MutableLiveData<>();
    private MutableLiveData<String>  albumname=new MutableLiveData<>();




    private final MutableLiveData<PlaybackStateCompat> playbackState = new MutableLiveData<>();
    private final MutableLiveData<MediaMetadataCompat> metadata = new MutableLiveData<>();

    private final SongRepo songRepository;
    public LiveData<List<Songmodel>> songs;

    public List<Artistmodel> artistmodels;

    public LiveData<List<Songmodel>> getSfavortwdsongs() {
        return sfavortwdsongs;
    }

    public MutableLiveData<List<Songmodel>> sfavortwdsongs=new MutableLiveData<>();
    public MutableLiveData<List<Songmodel>> searchsongs=new MutableLiveData<>();




    public LiveData<Songmodel> songmodel;





    private static final String TAG = SongViewModel.class.getSimpleName();

    public SongViewModel(SongRepo songRepository) {
        this.songRepository = songRepository;
    }

    public LiveData<List<Songmodel>> getSongs() {
        if (songs == null) {
            songs = songRepository.getSongs();
            sfavortwdsongs.setValue(songRepository.getfavsong());

        }
        return songs;
    }


    public LiveData<Songmodel> getMetadataSong() {
        songmodel=songRepository.getMetadata();

        return songmodel;
    }

    public LiveData<Integer> getseekCurrentPosition() {

        return  songRepository.getseekposition();
    }

    public void playselectedSong(int songId,String category) {
        songRepository.playselectedSong(songId,category);
    }


    public void playsong(){
       songRepository.playsong();

    }
    public  void searchsong(String song){
        searchsongs.setValue(songRepository.filterSongs(song));

    }

    public void pauseSong() {
        Log.d(TAG, "Pausing song");
        songRepository.pauseSong();
    }
    public void playforward(){
        songRepository.playforward();
    }

    public void   playbackward(){
        songRepository.playbackward();
    }



    public void seekTo(int progress){

        songRepository.seekTo(progress);

    }

    public void isfavorate(Songmodel song){
        songRepository.insertFavoriteSong(song);;

    }

    public void deletefavrate(Songmodel songmodel){
        songRepository.delete(songmodel);
    }
    public LiveData<List<Songmodel>> likedsongs() {


        return  songRepository.getlikedsongs();
    }

    public List<Artistmodel> getartist(){

        return songRepository.getArtist();

    }

    public  void getdsfnjknsd(){
        songRepository.getfff();
    }

    public LiveData<Integer> getcatgory(){
        return category;
    }

    public void setcategory(int pos){

        category.setValue(pos);

    }
    public LiveData<String> getartistname(){
        return artistname;
    }
    public LiveData<String> getalbumname(){
        return albumname;
    }
    public List<Songmodel> getselectedartist(String name){

        return songRepository.getselectedartist(name);
    }

    public List<Albummodel>getalbums(){
        return songRepository.getAlbum();
    }

    public List<Songmodel> getAlbumsongs(String name){


        return  songRepository.getselectedalbum(name);

    }

    public List<Filesmodel> getFiles(){
        return  songRepository.getFiles();
    }

    public LiveData<Boolean> getisplay(){


        return songRepository.getisplaying();
    }






}