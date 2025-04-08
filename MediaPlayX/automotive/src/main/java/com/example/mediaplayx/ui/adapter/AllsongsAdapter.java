package com.example.mediaplayx.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Songmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AllsongsAdapter extends RecyclerView.Adapter<AllsongsAdapter.ViewHolder> {
    private List<Songmodel> songs = new ArrayList<>();
    private OnSongClickListener listener;
    private ExecutorService executorService;
    private  byte[] albumArt;

    public interface OnSongClickListener {
        void onSongClick(int songId);
    }

    // Constructor
    public AllsongsAdapter(List<Songmodel> songs, OnSongClickListener listener) {
        if (songs != null) {
            this.songs.addAll(songs);
        }
        this.listener = listener;
        this.executorService = Executors.newFixedThreadPool(4); // Thread pool for background tasks
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView songImage;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.t12);
            songImage = view.findViewById(R.id.image1);
        }
    }

    @NonNull
    @Override
    public AllsongsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllsongsAdapter.ViewHolder holder, int position) {
        Songmodel song = songs.get(position);
        holder.textView.setText(song.getSong_name());

        // Load album art in a background thread
        executorService.execute(() -> {
            Bitmap albumArt = loadAlbumArt(song.getPath());
            holder.songImage.post(() -> {
                if (albumArt != null) {
                    holder.songImage.setImageBitmap(albumArt);
                } else {
                    holder.songImage.setImageResource(R.drawable.defaultimage); // Set a default image if no album art is found
                }
            });
        });


        holder.itemView.setOnClickListener(v -> {
            Log.d("song", "song clicked: " + song.getSong_name());
            listener.onSongClick(song.getSong_id());
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void updateSongs(List<Songmodel> newSongs) {
        this.songs.clear();
        if (newSongs != null) {
            this.songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }

    // Method to load album art
    private Bitmap loadAlbumArt(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(songPath);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (Exception e) {
            Log.e("AllsongsAdapter", "Error retrieving album art: " + e.getMessage());
        }
        return null;
    }

    private byte[] getbyte(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(songPath);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return art; // Return the byte array directly
            }
        } catch (Exception e) {
            Log.e("getbyte", "Error retrieving album art: " + e.getMessage());
        }
        return null;
    }

}