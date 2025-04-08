package com.example.mediaplayx.ui.view.fragments;

import static com.example.mediaplayx.data.repository.SongRepo.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Artistmodel;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.ui.adapter.AllsongsAdapter;
import com.example.mediaplayx.ui.viewmodel.SongViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Artiist_AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Artiist_AlbumFragment extends Fragment implements AllsongsAdapter.OnSongClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2,name;
    private RecyclerView recyclerView;
    private TextView textView;
    private AllsongsAdapter adapter;
    private List<Songmodel> songs;
    private Integer postion=-1;
    private SongViewModel songViewModel;
    private List<Artistmodel> artistmodels;

    public Artiist_AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtiistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Artiist_AlbumFragment newInstance(String param1, String param2) {
        Artiist_AlbumFragment fragment = new Artiist_AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_artiist, container, false);

        Button but=view.findViewById(R.id.backbutton);
        recyclerView=view.findViewById(R.id.artistsongs);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textView=view.findViewById(R.id.artist_name);
        adapter=new AllsongsAdapter(new ArrayList<>(),this);
        songViewModel = new ViewModelProvider(getActivity()).get(SongViewModel.class);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();

                fragmentManager.popBackStack();

            }
        });


        return view;


    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            observecategory();
        if (getArguments() != null) {
             name= getArguments().getString("artist_name");
            textView.setText(name);}

        updaselection();





    }

    @Override
    public void onSongClick(int songId) {


        if(postion==1){
            songViewModel.playselectedSong(songId,"ARTIST");

        } else if (postion==2) {
            songViewModel.playselectedSong(songId,"ALBUM");

        }


    }

    public  void observecategory(){
        songViewModel.getcatgory().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                postion=integer;
                Log.d(TAG, "onChanged: ddddddddddddddddddddd"+postion);
                updaselection();

            }
        });
    }
    public  void updaselection(){
        switch (postion) {
            case 1:
                songs = songViewModel.getselectedartist(name);
                recyclerView.setAdapter(adapter);
                adapter.updateSongs(songs);
                break;
            case 2:
                songs=songViewModel.getAlbumsongs(name);
                recyclerView.setAdapter(adapter);
                adapter.updateSongs(songs);

                break;
                
            case 4:
                songViewModel.getdsfnjknsd();


                break;


        }

    }
}