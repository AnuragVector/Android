package com.example.mediaplayx.ui.view.fragments;

import static com.example.mediaplayx.R.drawable.*;
import static com.example.mediaplayx.data.repository.SongRepo.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Songmodel;
import com.example.mediaplayx.data.repository.SongRepo;
import com.example.mediaplayx.ui.adapter.AllsongsAdapter;
import com.example.mediaplayx.ui.viewmodel.SongViewModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Playbarfragment extends Fragment {
    private static final String TAG = Playbarfragment.class.getSimpleName();

    private static final String ARG_SONGS = "songs";

    private List<Songmodel> songs, fav=new ArrayList<>();
    private int selectedSongId = -1;
    private TextView duration,progresduration;
    private SongViewModel songViewModel;
    private boolean first=false;
    private Songmodel sonfav;
    private ImageView playPauseButton, songimage, playforward, playbackward;
    private SongRepo songRepo;
    private SeekBar seekBar;

    private Button likeButton;
    private boolean isPlaying;
    private int postion=-1;
    private long min,sec;
    private String artistname="",albumname="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        songRepo = SongRepo.getInstance(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playbar, container, false);

        playPauseButton = view.findViewById(R.id.play);
        duration=view.findViewById(R.id.duratuon);
        songimage = view.findViewById(R.id.songimage);
        playforward = view.findViewById(R.id.playforward);
        playbackward = view.findViewById(R.id.playbackward);
        seekBar = view.findViewById((R.id.seek));
        songViewModel = new ViewModelProvider(getActivity()).get(SongViewModel.class);
        likeButton = view.findViewById(R.id.likebutton);
        progresduration=view.findViewById(R.id.progreetime);
        Typeface typeface = ResourcesCompat.getFont(duration.getContext(), R.font.custom_font);
        first=false;

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isfavourates();
        changemetatadata();

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (isPlaying) {
                    songViewModel.pauseSong();
                } else {
                    animateButton();
                    songViewModel.playsong();

                }
            }
        });
        playforward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                songViewModel.playforward();


            }
        });
        playbackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                        songViewModel.playbackward();


            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    songViewModel.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });


    }


    public void changemetatadata() {

        songViewModel.getcatgory().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                postion=integer;

            }
        });

        songViewModel.getisplay().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isPlaying = aBoolean;
                if (isPlaying) {
                    playPauseButton.setImageResource(baseline_pause_circle_24);


                } else {
                    animateButton();

                    playPauseButton.setImageResource(baseline_play_circle_24);
                    animateButton();
                }

            }
        });
        songViewModel.getMetadataSong().observe(getViewLifecycleOwner(), new Observer<Songmodel>() {
            @Override
            public void onChanged(Songmodel songmodel) {
                String songpath = songmodel.getPath();
                selectedSongId = songmodel.getSong_id();
                loadAlbumArt(songpath);
                sonfav = songmodel;
                 min = TimeUnit.MILLISECONDS.toMinutes(songmodel.getDuration());
                 sec = TimeUnit.MILLISECONDS.toSeconds(songmodel.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(min);
                duration.setText(String.format("%d:%d", min, sec));

                updateLikeButton();

                seekBar.setMax((int) songmodel.getDuration());
                animateButton();

            }
        });
        songViewModel.getseekCurrentPosition().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer position) {

                seekBar.setProgress(position);
                convertMillisToMinSec(position);


            }
        });

    }


    private void animateButton() {
        playPauseButton.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(100)
                .withEndAction(() -> playPauseButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
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

    private void isfavourates() {
        likeButton.setOnClickListener(v -> {
            if (sonfav != null) {
                boolean isLiked = fav.stream().anyMatch(song -> song.getSong_id() == selectedSongId);

                if (isLiked) {
                    songViewModel.deletefavrate(sonfav);
                    likeButton.setBackgroundResource(R.drawable.dislike);
                } else {
                    songViewModel.isfavorate(sonfav);
                    likeButton.setBackgroundResource(R.drawable.like);
                }
            } else {
                Log.e(TAG, "isfavourates: sonfav is null");
            }

        });
        songViewModel.likedsongs().observe(getViewLifecycleOwner(), new Observer<List<Songmodel>>() {
            @Override
            public void onChanged(List<Songmodel> songmodels) {
                Log.d(TAG, "onChanged : "+songmodels);
                fav = songmodels;
                updateLikeButton();

            }
        });
//        songViewModel.getSfavortwdsongs().observe(getViewLifecycleOwner(), new Observer<List<Songmodel>>() {
//            @Override
//            public void onChanged(List<Songmodel> songmodels) {
//                fav = songmodels;
//
//            }
//        });
    }

    private void updateLikeButton() {
        if (fav != null) {
            boolean isLiked;
            if (fav.stream().anyMatch(song -> song.getSong_id() == selectedSongId)) {
                sonfav.setFavorate(true);
                isLiked = true;
            } else isLiked = false;
            likeButton.setBackgroundResource(isLiked ? R.drawable.like : R.drawable.dislike);
        }

    }

        public void  convertMillisToMinSec(long millis) {
            long min = TimeUnit.MILLISECONDS.toMinutes(millis);
            long sec = TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(min);

            progresduration.setText(String.format("%d:%d", min, sec));
        }


}