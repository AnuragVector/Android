package com.example.mediaplayx.ui.adapter;

import static com.example.mediaplayx.data.repository.SongRepo.TAG;

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

import com.bumptech.glide.Glide;
import com.example.mediaplayx.R;
import com.example.mediaplayx.data.model.Songmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoratesAdapter extends RecyclerView.Adapter<FavoratesAdapter.ViewHolder> {
    private List<Songmodel> favsongs ;
    private   OnfavorateClickListener listener;
    private ExecutorService executorService;

    public interface OnfavorateClickListener{
        void onfavClick(int songId);
    }
    public FavoratesAdapter(List<Songmodel> song, OnfavorateClickListener listener) {
        this.favsongs = song != null ? song : new ArrayList<>(); // Initialize with an empty list if null
        this.listener = listener;
        this.executorService = Executors.newFixedThreadPool(4);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private ImageView songimage;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.t12);
            songimage = view.findViewById(R.id.image1);

        }

        public TextView getTextView() {
            return textView;
        }
    }


    public  FavoratesAdapter(List<Songmodel> songmodels){

        this.favsongs=songmodels;

    }


    @NonNull
    @Override
    public FavoratesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.songs, parent, false);
        Log.d(TAG, "onBindViewHolder: ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Songmodel s = favsongs.get(position);
        holder.textView.setText(s.getSong_name());
        executorService.execute(() -> {
            Bitmap albumArt = loadAlbumArt(s.getPath());
            holder.songimage.post(() -> {
                if (albumArt != null) {
                    Glide.with(holder.songimage.getContext())
                            .load(albumArt)
                            .placeholder(R.drawable.defaultimage)
                            .into(holder.songimage);
                } else {
                    holder.songimage.setImageResource(R.drawable.defaultimage);
                }
            });
        });
        holder.itemView.setOnClickListener(v -> {
            Log.d("song", "song clicked: " + s.getSong_id());
         listener.onfavClick(s.getSong_id()); //

        });

    }

    @Override
    public int getItemCount() {
        return favsongs.size();
    }

    public void updateSongs(List<Songmodel> newSongs) {
       this.favsongs.clear();
        Log.d(TAG, "updateSongs: rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
        if (newSongs != null) {
            this.favsongs.addAll(newSongs);

        }
        notifyDataSetChanged();

    }
    private Bitmap loadAlbumArt(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(songPath);
            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                return BitmapFactory.decodeByteArray(art, 0, art.length);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving album art: " + e.getMessage());
        }
        return null;
    }

}
