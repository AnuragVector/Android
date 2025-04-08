package com.example.mediaplayx.ui.view.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.repository.SongRepo;
import com.example.mediaplayx.ui.adapter.AlbumsAdapter;
import com.example.mediaplayx.ui.adapter.AllsongsAdapter;
import com.example.mediaplayx.ui.adapter.Artistadapter;
import com.example.mediaplayx.ui.adapter.FavoratesAdapter;
import com.example.mediaplayx.ui.adapter.Filesadapter;
import com.example.mediaplayx.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

public class Mediafragment extends Fragment implements AllsongsAdapter.OnSongClickListener, FavoratesAdapter.OnfavorateClickListener, Artistadapter.ArtistOnclick, AlbumsAdapter.AlbumOnclick ,Filesadapter.FilesOnclick{
    private String[] menu = {"Songs", "Artist", "Album", "Favorite songs","FIles"};
    private RecyclerView recyclerView;
    private AllsongsAdapter songsAdapter;
    private SongViewModel model;
    private Integer position = 0;
    private static final String TAG = Mediafragment.class.getSimpleName();
    private List<Songmodel> songs;
    private List<Songmodel> allSongs;
    private int selectedSongId = -1;
    private SongViewModel songViewModel;
    private ImageView songimage;
    private SongRepo songRepo;
    private FavoratesAdapter favoratesAdapter;

    private Filesadapter filesadapter;


    private AlbumsAdapter albumAdapter;
    private Artistadapter artistadapter;
    private Artiist_AlbumFragment artiistFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);


        recyclerView = view.findViewById(R.id.toggle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songRepo = SongRepo.getInstance(getContext());

        songsAdapter = new AllsongsAdapter(new ArrayList<>(), this);
        albumAdapter = new AlbumsAdapter(new ArrayList<>(), this);
        favoratesAdapter = new FavoratesAdapter(new ArrayList<>(), this);

//
        songViewModel = new ViewModelProvider(getActivity()).get(SongViewModel.class);
        artistadapter = new Artistadapter(new ArrayList<>(), this);
        artiistFragment = new Artiist_AlbumFragment();
        filesadapter=new Filesadapter(new ArrayList<>(),this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observefavorates();

        observeSongs();
        observecategory();



        updateSelectedItem(this.position);
//
    }


    public void updateSelectedItem(int positon) {
        this.position = positon;
        switch (this.position) {
            case 0:
                songsAdapter.updateSongs(songViewModel.getSongs().getValue());
                recyclerView.setAdapter(songsAdapter);
                Log.d("Mediacategoryfragment", "Selected Songs");
                break;
            case 1:
                artistadapter.updateArtist(songViewModel.getartist());
                recyclerView.setAdapter(artistadapter);

                break;
            case 2:

                albumAdapter.updateAlbum(songViewModel.getalbums());
                Log.d("Mediacategoryfragment", "Selected Albums");
                recyclerView.setAdapter(albumAdapter);
                break;

            case 3:
                favoratesAdapter.updateSongs(songs);
                recyclerView.setAdapter(favoratesAdapter);

                break;

            case 4:
//                filesadapter.updateFiles();
            default:
                break;
        }
    }

    private void observeSongs() {
        songViewModel.searchsongs.observe(getViewLifecycleOwner(), new Observer<List<Songmodel>>() {
            @Override
            public void onChanged(List<Songmodel> songmodels) {
                updatesearchsongs(songmodels);
            }
        });

    }

    public void observefavorates() {
        songViewModel.likedsongs().observe(getViewLifecycleOwner(), new Observer<List<Songmodel>>() {
            @Override
            public void onChanged(List<Songmodel> songmodels) {
                songs=songmodels;
                favoratesAdapter.updateSongs(songmodels);

            }
        });
    }

    private void loadAlbumArt(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(songPath);
            byte[] art = retriever.getEmbeddedPicture();

            if (art != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                songimage.setImageBitmap(bitmap);
            } else {
                songimage.setImageResource(R.drawable.defaultimage);

            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving album art: " + e.getMessage());

        }

    }

    @Override
    public void onSongClick(int songId) {
        songViewModel.playselectedSong(songId, "ALLSONGS");

    }

    @Override
    public void onfavClick(int songId) {
        songViewModel.playselectedSong(songId, "FAVORATES");

    }


    @Override
    public void onArtistClick(String artist) {

        Log.d(TAG, "onArtistClick: " + artist);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("artist_name", artist);
        artiistFragment.setArguments(bundle);
        transaction.replace(R.id.fragment, artiistFragment); // Use your container ID
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void observecategory() {
        songViewModel.getcatgory().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                updateSelectedItem(integer);
            }
        });
    }

    @Override
    public void onAlbumClick(String albumname) {


        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("artist_name", albumname);
        artiistFragment.setArguments(bundle);
        transaction.replace(R.id.fragment, artiistFragment); // Use your container ID
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void updatesearchsongs(List<Songmodel> songmodels) {
        recyclerView.setAdapter(songsAdapter);
        songsAdapter.updateSongs(songmodels);


    }

    @Override
    public void onFilesClick(String FIlename) {

    }
}