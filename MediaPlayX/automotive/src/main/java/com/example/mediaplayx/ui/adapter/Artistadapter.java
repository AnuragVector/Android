package com.example.mediaplayx.ui.adapter;

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
import com.example.mediaplayx.data.model.Artistmodel;
import com.example.mediaplayx.data.model.Songmodel;

import java.util.ArrayList;
import java.util.List;

public class Artistadapter extends RecyclerView.Adapter<Artistadapter.ViewHolder> {
    private List<Artistmodel> artistmodels;
    private ArtistOnclick listener;


    // Constructor
    public interface ArtistOnclick{
        void onArtistClick(String artistname);
    }
    public Artistadapter(List<Artistmodel> artistmodels,ArtistOnclick listener) {
        this.artistmodels = artistmodels != null ? artistmodels : new ArrayList<>();
        this.listener=listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView songimage;
        private MediaMetadataRetriever retriever;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tartist);
            songimage = view.findViewById(R.id.image1);
            retriever = new MediaMetadataRetriever();
        }

        public TextView getTextView() {
            return textView;
        }
    }

    @NonNull
    @Override
    public Artistadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist, parent, false);

        return new Artistadapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Artistadapter.ViewHolder holder, int position) {
        Artistmodel s = artistmodels.get(position);
        holder.textView.setText(s.getArtist_name());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArtistClick(s.getArtist_name());

            }
        });

    }


    @Override
    public int getItemCount() {
        return artistmodels.size();
    }
    public void updateArtist(List<Artistmodel> a ) {
        this.artistmodels.clear();
        if (a != null) {
            this.artistmodels.addAll(a);

        }
        notifyDataSetChanged();

    }
}

